package com.uaa.registro_uaa


import android.graphics.Color
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class verManzanasActivity: AppCompatActivity() {

    private lateinit var map: MapView
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private lateinit var myLocationOverlay: MyLocationNewOverlay
    private var myCurrentLocation: GeoPoint? = null

    private lateinit var tvInfo: TextView
    private var totalManzanas = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.getInstance().load(
            applicationContext,
            getSharedPreferences("osmdroid", MODE_PRIVATE)
        )

        setContentView(R.layout.activity_ver_manzanas)

        initializeViews()
        setupMap()
        setupLocationOverlay()
        setupButtons()
        cargarTodasLasManzanas()
    }

    private fun initializeViews() {
        map = findViewById(R.id.mapView)
        tvInfo = findViewById(R.id.tvInfoManzanas)
    }

    private fun setupMap() {
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
        val controller = map.controller
        controller.setZoom(15.0)
        controller.setCenter(GeoPoint(21.8853, -102.2916))
    }

    private fun setupLocationOverlay() {
        myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this), map)
        myLocationOverlay.enableMyLocation()
        map.overlays.add(myLocationOverlay)

        myLocationOverlay.runOnFirstFix {
            runOnUiThread {
                myCurrentLocation = myLocationOverlay.myLocation
            }
        }
    }

    private fun setupButtons() {
        findViewById<ImageButton>(R.id.btnMyLocation).setOnClickListener {
            myCurrentLocation?.let {
                map.controller.animateTo(it)
                map.controller.setZoom(18.0)
            } ?: Toast.makeText(this, "Obteniendo ubicación...", Toast.LENGTH_SHORT).show()
        }

        findViewById<ImageButton>(R.id.btnClose).setOnClickListener {
            finish()
        }
    }

    private fun cargarTodasLasManzanas() {
        tvInfo.text = "⏳ Cargando manzanas..."

        // Opción 1: Cargar solo las del usuario actual
        val userId = auth.currentUser?.uid

        val query = if (userId != null) {
            // Solo manzanas del usuario
            db.collection("manzanas").whereEqualTo("userId", userId)
        } else {
            // Todas las manzanas
            db.collection("manzanas")
        }

        query.get()
            .addOnSuccessListener { snapshot ->
                totalManzanas = snapshot.size()

                if (totalManzanas == 0) {
                    tvInfo.text = "📍 No hay manzanas registradas"
                    return@addOnSuccessListener
                }

                tvInfo.text = "📍 Cargando $totalManzanas manzanas..."

                val colores = listOf(
                    Pair(Color.argb(50, 76, 175, 80), Color.rgb(56, 142, 60)),    // Verde
                    Pair(Color.argb(50, 33, 150, 243), Color.rgb(25, 118, 210)),  // Azul
                    Pair(Color.argb(50, 255, 152, 0), Color.rgb(245, 124, 0)),    // Naranja
                    Pair(Color.argb(50, 156, 39, 176), Color.rgb(123, 31, 162)),  // Morado
                    Pair(Color.argb(50, 244, 67, 54), Color.rgb(211, 47, 47)),    // Rojo

                )

                var contador = 0

                for (doc in snapshot.documents) {
                    val manzanaNum = doc.getLong("manzanaNumero")?.toInt() ?: 0
                    val puntosData = doc.get("puntosPerimetro") as? List<HashMap<String, Double>>
                    val centroLat = doc.getDouble("centroLatitud") ?: 0.0
                    val centroLon = doc.getDouble("centroLongitud") ?: 0.0
                    val timestamp = doc.getLong("timestamp") ?: 0
                    val email = doc.getString("usuarioEmail") ?: "Desconocido"
                    val docId = doc.id

                    if (puntosData != null && puntosData.isNotEmpty()) {
                        val puntos = puntosData.map {
                            GeoPoint(it["lat"] ?: 0.0, it["lon"] ?: 0.0)
                        }.toMutableList()

                        // Cerrar polígono
                        puntos.add(puntos[0])

                        // Color rotativo
                        val colorPair = colores[contador % colores.size]

                        // Dibujar polígono
                        val polygon = Polygon(map).apply {
                            points = ArrayList(puntos)
                            fillColor = colorPair.first
                            strokeColor = colorPair.second
                            strokeWidth = 3f
                            title = "Manzana #$manzanaNum"
                        }

                        map.overlays.add(polygon)

                        // Marcador central con toda la info
                        val marker = Marker(map)
                        marker.position = GeoPoint(centroLat, centroLon)
                        marker.title = "ID del alumno:$manzanaNum"

                        val fecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                            .format(Date(timestamp))

                        marker.snippet = buildString {
                            append("${puntosData.size} esquinas\n\n\n")
                            append("$fecha\n\n")
                            append("$email\n\n")
                            append("\n━━━━━━━━━━━━━━━━━━━\n\n\n")
                            append("\n${String.format("%.5f", centroLat)}, ${String.format("%.5f", centroLon)}")
                        }

                        marker.icon = getManzanaMarker(contador % colores.size)
                        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

                        // Obtener dirección (opcional)
                        obtenerDireccionParaMarcador(marker, GeoPoint(centroLat, centroLon))

                        map.overlays.add(marker)
                        contador++
                    }
                }

                map.invalidate()

                // Ajustar vista para mostrar todas las manzanas
                if (contador > 0) {
                    tvInfo.text = "📍 $contador manzanas registradas (Solo lectura)"
                    Toast.makeText(this, "✓ $contador manzanas cargadas", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { e ->
                tvInfo.text = "❌ Error al cargar manzanas"
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun obtenerDireccionParaMarcador(marker: Marker, punto: GeoPoint) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val direccion = GeocodingUtils.obtenerDireccionSimplificada(punto)
                marker.snippet = "${marker.snippet}\n\n $direccion"
                map.invalidate()
            } catch (e: Exception) {
                // Ignorar error
            }
        }
    }

    private fun getManzanaMarker(index: Int): android.graphics.drawable.Drawable? {
        val colores = listOf(
            Color.rgb(56, 142, 60),
            Color.rgb(25, 118, 210),
            Color.rgb(245, 124, 0),
            Color.rgb(123, 31, 162),
            Color.rgb(211, 47, 47),
            Color.rgb(0, 121, 107)
        )

        val drawable = ContextCompat.getDrawable(this, R.drawable.ic_marker)?.mutate()
        drawable?.let { DrawableCompat.setTint(it, colores[index % colores.size]) }
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



