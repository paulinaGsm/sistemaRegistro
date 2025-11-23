package com.uaa.registro_uaa

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.google.firebase.firestore.FirebaseFirestore
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class MapaActivityDocente : AppCompatActivity() {

    private lateinit var map: MapView
    private val db = FirebaseFirestore.getInstance()
    private var studentUid: String? = null // UID del estudiante a visualizar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configuración básica OSMDroid
        Configuration.getInstance().load(
            applicationContext,
            getSharedPreferences("osmdroid", MODE_PRIVATE)
        )

        setContentView(R.layout.activity_mapa_docente)

        // Obtener el UID del estudiante desde el Intent
        studentUid = intent.getStringExtra("studentUid")

        if (studentUid == null) {
            Toast.makeText(this, "Error: No se recibió información del estudiante", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        map = findViewById(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)

        val controller = map.controller
        controller.setZoom(15.0)
        controller.setCenter(GeoPoint(21.8853, -102.2916))

        // ✅ Botón para centrar en las ubicaciones del estudiante
        val btnCentrar = findViewById<ImageButton>(R.id.btnCentrarUbicaciones)
        btnCentrar.setOnClickListener {
            centrarEnUbicaciones()
        }

        // ✅ SOLO cargar ubicaciones de este estudiante específico
        loadStudentLocations()

        //MapEventsOverlay evita agregar marcadores
    }

    /**
     * Cargar SOLO las ubicaciones del estudiante seleccionado
     */
    private fun loadStudentLocations() {
        if (studentUid == null) return

        db.collection("usuarios")
            .document(studentUid!!)
            .collection("locations")
            .get()
            .addOnSuccessListener { locSnapshot ->
                // Limpiar marcadores anteriores
                map.overlays.clear()

                val geoPoints = mutableListOf<GeoPoint>()

                if (locSnapshot.isEmpty) {
                    Toast.makeText(this, "Este estudiante no tiene ubicaciones registradas", Toast.LENGTH_LONG).show()
                    return@addOnSuccessListener
                }

                // Obtener nombre del estudiante
                db.collection("usuarios")
                    .document(studentUid!!)
                    .get()
                    .addOnSuccessListener { userDoc ->
                        val studentName = userDoc.getString("nombre") ?: ""
                        val studentEmail = userDoc.getString("email") ?: ""

                        // Agregar marcadores
                        var index = 1
                        for (locDoc in locSnapshot.documents) {
                            val lat = locDoc.getDouble("Latitud")
                            val lon = locDoc.getDouble("Longitud")
                            val timestamp = locDoc.getLong("timestamp")

                            if (lat != null && lon != null) {
                                val point = GeoPoint(lat, lon)
                                geoPoints.add(point)

                                val marker = Marker(map)
                                marker.position = point
                                marker.title = "📍 Ubicación $index"

                                val fecha = if (timestamp != null) {
                                    val sdf = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
                                    sdf.format(java.util.Date(timestamp))
                                } else {
                                    "Fecha desconocida"
                                }

                                marker.snippet = """
                                    Estudiante: $studentName
                                    Email: $studentEmail
                                    Fecha: $fecha
                                    Lat: ${String.format("%.6f", lat)}
                                    Lon: ${String.format("%.6f", lon)}
                                """.trimIndent()

                                marker.icon = getColoredMarker(Color.parseColor("#FF5722")) // Naranja para destacar
                                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

                                map.overlays.add(marker)
                                index++
                            }
                        }

                        map.invalidate()

                        // Ajustar zoom para mostrar todas las ubicaciones
                        if (geoPoints.isNotEmpty()) {
                            centrarEnPuntos(geoPoints)
                            Toast.makeText(
                                this,
                                "${geoPoints.size} ubicaciones cargadas de $studentName",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar ubicaciones: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    /**
     * Centrar el mapa en todas las ubicaciones
     */
    private fun centrarEnUbicaciones() {
        val puntos = mutableListOf<GeoPoint>()

        map.overlays.forEach { overlay ->
            if (overlay is Marker) {
                puntos.add(overlay.position)
            }
        }

        if (puntos.isNotEmpty()) {
            centrarEnPuntos(puntos)
        } else {
            Toast.makeText(this, "No hay ubicaciones para mostrar", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Centrar y ajustar zoom para mostrar todos los puntos
     */
    private fun centrarEnPuntos(puntos: List<GeoPoint>) {
        if (puntos.isEmpty()) return

        if (puntos.size == 1) {
            // Solo una ubicación - centrar con zoom específico
            map.controller.setCenter(puntos[0])
            map.controller.setZoom(17.0)
        } else {
            // Múltiples ubicaciones - ajustar para mostrar todas
            val boundingBox = BoundingBox.fromGeoPoints(puntos)
            map.zoomToBoundingBox(boundingBox, true, 100)
        }
    }

    /**
     * Crear marcador con color personalizado
     */
    private fun getColoredMarker(color: Int): Drawable? {
        val drawable = ContextCompat.getDrawable(this, R.drawable.ic_marker)?.mutate()
        drawable?.let {
            DrawableCompat.setTint(it, color)
        }
        return drawable
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }
}