package com.uaa.registro_uaa

import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MapaManzanaActivity : AppCompatActivity() {

    private lateinit var map: MapView
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private lateinit var myLocationOverlay: MyLocationNewOverlay
    private var myCurrentLocation: GeoPoint? = null

    // Para manzanas
    private val puntosPerimetro = mutableListOf<GeoPoint>()
    private var poligonoActual: Polygon? = null
    private val marcadoresPerimetro = mutableListOf<Marker>()

    private lateinit var tvContadorPuntos: TextView
    private lateinit var btnFinalizarManzana: Button
    private lateinit var btnLimpiarManzana: Button
    private lateinit var btnGuardarManzana: Button

    private var manzanaNumero: Int = 0
    private var modoTrazado = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.getInstance().load(
            applicationContext,
            getSharedPreferences("osmdroid", MODE_PRIVATE)
        )

        setContentView(R.layout.activity_mapa_manzana)

        // Obtener número de manzana
        manzanaNumero = intent.getIntExtra("MANZANA_NUM", 0)

        initializeViews()
        setupMap()
        setupLocationOverlay()
        setupButtons()
        setupMapEvents()

        // Cargar manzanas existentes
        loadManzanasFromFirestore()
    }

    private fun initializeViews() {
        map = findViewById(R.id.map)
        tvContadorPuntos = findViewById(R.id.tvContadorPuntos)
        btnFinalizarManzana = findViewById(R.id.btnFinalizarManzana)
        btnLimpiarManzana = findViewById(R.id.btnLimpiarManzana)
        btnGuardarManzana = findViewById(R.id.btnGuardarManzana)
    }

    private fun setupMap() {
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
        val controller = map.controller
        controller.setZoom(18.0) // Mayor zoom para ver manzanas
        controller.setCenter(GeoPoint(21.8853, -102.2916))
    }

    private fun setupLocationOverlay() {
        myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this), map)
        myLocationOverlay.enableMyLocation()
        myLocationOverlay.enableFollowLocation()
        map.overlays.add(myLocationOverlay)

        myLocationOverlay.runOnFirstFix {
            runOnUiThread {
                myCurrentLocation = myLocationOverlay.myLocation
                myCurrentLocation?.let {
                    map.controller.animateTo(it)
                }
            }
        }
    }

    private fun setupButtons() {
        // Botón Mi Ubicación
        findViewById<ImageButton>(R.id.btnMyLocation).setOnClickListener {
            myCurrentLocation?.let {
                map.controller.animateTo(it)
            } ?: Toast.makeText(this, "Obteniendo ubicación...", Toast.LENGTH_SHORT).show()
        }

        // Botón Iniciar Trazado
        findViewById<Button>(R.id.btnIniciarTrazado).setOnClickListener {
            iniciarTrazadoManzana()
        }

        // Botón Finalizar Manzana (cerrar polígono)
        btnFinalizarManzana.setOnClickListener {
            finalizarTrazado()
        }

        // Botón Limpiar
        btnLimpiarManzana.setOnClickListener {
            limpiarTrazado()
        }

        // Botón Guardar
        btnGuardarManzana.setOnClickListener {
            guardarManzanaEnFirestore()
        }
    }

    private fun setupMapEvents() {
        val mapEventsReceiver = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                if (modoTrazado && p != null) {
                    agregarPuntoPerimetro(p)
                }
                return true
            }

            override fun longPressHelper(p: GeoPoint?): Boolean {
                return false
            }
        }

        val eventsOverlay = MapEventsOverlay(mapEventsReceiver)
        map.overlays.add(eventsOverlay)
    }

    // ========== FUNCIONES DE TRAZADO ==========

    private fun iniciarTrazadoManzana() {
        modoTrazado = true
        limpiarTrazado()
        Toast.makeText(this, "Toca el mapa para marcar esquinas de la manzana", Toast.LENGTH_LONG).show()
        btnFinalizarManzana.isEnabled = false
        btnGuardarManzana.isEnabled = false
    }

    private fun agregarPuntoPerimetro(punto: GeoPoint) {
        puntosPerimetro.add(punto)

        // Crear marcador numerado
        val marker = Marker(map)
        marker.position = punto
        marker.title = "Punto ${puntosPerimetro.size}"
        marker.icon = getNumberedMarker(puntosPerimetro.size)
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)

        marcadoresPerimetro.add(marker)
        map.overlays.add(marker)

        // Actualizar contador
        tvContadorPuntos.text = "Puntos marcados: ${puntosPerimetro.size}"

        // Dibujar polígono temporal
        if (puntosPerimetro.size >= 2) {
            dibujarPoligonoTemporal()
            btnFinalizarManzana.isEnabled = puntosPerimetro.size >= 3
        }

        map.invalidate()
    }

    private fun dibujarPoligonoTemporal() {
        // Remover polígono anterior
        poligonoActual?.let { map.overlays.remove(it) }

        // Crear nuevo polígono
        poligonoActual = Polygon(map).apply {
            points = ArrayList(puntosPerimetro)
            fillColor = Color.argb(50, 0, 150, 255) // Azul semi-transparente
            strokeColor = Color.BLUE
            strokeWidth = 3f
            outlinePaint.strokeCap = Paint.Cap.ROUND
        }

        map.overlays.add(poligonoActual)
        map.invalidate()
    }

    private fun finalizarTrazado() {
        if (puntosPerimetro.size < 3) {
            Toast.makeText(this, "Se necesitan al menos 3 puntos", Toast.LENGTH_SHORT).show()
            return
        }

        // Cerrar el polígono agregando el primer punto al final
        val puntosCerrados = ArrayList(puntosPerimetro)
        puntosCerrados.add(puntosPerimetro[0])

        // Calcular centro para mostrar información
        val centroLat = puntosPerimetro.map { it.latitude }.average()
        val centroLon = puntosPerimetro.map { it.longitude }.average()
        val centro = GeoPoint(centroLat, centroLon)

        // Actualizar polígono con color final (verde)
        poligonoActual?.let { map.overlays.remove(it) }

        poligonoActual = Polygon(map).apply {
            points = puntosCerrados
            fillColor = Color.argb(70, 0, 255, 0) // Verde semi-transparente
            strokeColor = Color.rgb(0, 150, 0)
            strokeWidth = 4f
            title = "Manzana #$manzanaNumero"
            snippet = "${puntosPerimetro.size} puntos marcados"
        }

        map.overlays.add(poligonoActual)

        // Agregar marcador en el centro con info
        val markerCentro = Marker(map)
        markerCentro.position = centro
        markerCentro.title = "🏘️ Manzana #$manzanaNumero"
        markerCentro.snippet = "Centro de la manzana\n${puntosPerimetro.size} esquinas trazadas"
        markerCentro.icon = getCentroMarker()
        markerCentro.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

        map.overlays.add(markerCentro)
        map.invalidate()

        modoTrazado = false
        btnGuardarManzana.isEnabled = true
        btnFinalizarManzana.isEnabled = false

        // Obtener dirección (opcional, usando coroutines)
        obtenerYMostrarDireccion(centro)

        Toast.makeText(this, "✓ Manzana trazada. Ahora puedes guardar", Toast.LENGTH_LONG).show()
    }

    private fun obtenerYMostrarDireccion(centro: GeoPoint) {
        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
            try {
                val direccion = GeocodingUtils.obtenerDireccionSimplificada(centro)
                tvContadorPuntos.text = "📍 $direccion"
            } catch (e: Exception) {
                // Ignorar si falla}
                tvContadorPuntos.text = "Puntos marcados: ${puntosPerimetro.size}"
            }
        }
    }


    private fun getCentroMarker(): android.graphics.drawable.Drawable? {
        val drawable = ContextCompat.getDrawable(this, R.drawable.ic_marker)?.mutate()
        drawable?.let { DrawableCompat.setTint(it, Color.rgb(255, 152, 0)) }
        return drawable
    }

    private fun limpiarTrazado() {
        // Remover marcadores
        marcadoresPerimetro.forEach { map.overlays.remove(it) }
        marcadoresPerimetro.clear()

        // Remover polígono
        poligonoActual?.let { map.overlays.remove(it) }
        poligonoActual = null

        // Limpiar lista
        puntosPerimetro.clear()

        // Reset UI
        tvContadorPuntos.text = "Puntos marcados: 0"
        btnFinalizarManzana.isEnabled = false
        btnGuardarManzana.isEnabled = false

        map.invalidate()
    }

    // ========== GUARDAR EN FIRESTORE ==========

    private fun guardarManzanaEnFirestore() {
        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        if (puntosPerimetro.isEmpty()) {
            Toast.makeText(this, "No hay puntos para guardar", Toast.LENGTH_SHORT).show()
            return
        }

        // Convertir puntos a formato serializable
        val puntosData = puntosPerimetro.map {
            hashMapOf("lat" to it.latitude, "lon" to it.longitude)
        }

        // Calcular centro de la manzana
        val centroLat = puntosPerimetro.map { it.latitude }.average()
        val centroLon = puntosPerimetro.map { it.longitude }.average()

        // Obtener ID del documento de la Tarjeta_Salud (si existe)
        val idDocumento = intent.getStringExtra("ID_DOCUMENTO")

        val manzanaData = hashMapOf(
            "manzanaNumero" to manzanaNumero,
            "puntosPerimetro" to puntosData,
            "centroLatitud" to centroLat,
            "centroLongitud" to centroLon,
            "timestamp" to System.currentTimeMillis(),
            "usuarioEmail" to user.email,
            "userId" to user.uid,
            "idDocumentoTarjeta" to (idDocumento ?: "")
        )

        db.collection("manzanas")
            .add(manzanaData)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(this, "✓ Manzana ID$manzanaNumero guardada", Toast.LENGTH_LONG).show()

                // OPCIONAL: También actualizar referencia en Tarjeta_Salud
                if (idDocumento != null) {
                    val manzanaRef = hashMapOf(
                        "manzanaId" to documentReference.id,
                        "manzanaNumero" to manzanaNumero,
                        "centroLatitud" to centroLat,
                        "centroLongitud" to centroLon
                    )

                    db.collection("Tarjeta_Salud")
                        .document(idDocumento)
                        .update("datosUbicacionManzana", manzanaRef)
                }

                // Regresar resultado
                val resultIntent = Intent()
                resultIntent.putExtra("NUM_PUNTOS", puntosPerimetro.size)
                resultIntent.putExtra("CENTRO_LAT", centroLat)
                resultIntent.putExtra("CENTRO_LON", centroLon)
                setResult(RESULT_OK, resultIntent)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // ========== CARGAR MANZANAS EXISTENTES ==========

    private fun loadManzanasFromFirestore() {
        db.collection("manzanas")
            .get()
            .addOnSuccessListener { snapshot ->
                for (doc in snapshot.documents) {
                    val manzanaNum = doc.getLong("manzanaNumero")?.toInt() ?: 0
                    val puntosData = doc.get("puntosPerimetro") as? List<HashMap<String, Double>>

                    if (puntosData != null && puntosData.isNotEmpty()) {
                        val puntos = puntosData.map {
                            GeoPoint(it["lat"] ?: 0.0, it["lon"] ?: 0.0)
                        }.toMutableList()

                        // Cerrar polígono
                        puntos.add(puntos[0])

                        // Dibujar polígono
                        val polygon = Polygon(map).apply {
                            points = ArrayList(puntos)
                            fillColor = Color.argb(40, 255, 165, 0) // Naranja
                            strokeColor = Color.rgb(255, 140, 0)
                            strokeWidth = 3f
                            title = "Manzana #$manzanaNum"
                        }

                        map.overlays.add(polygon)
                    }
                }
                map.invalidate()
            }
    }

    // ========== UTILIDADES ==========

    private fun getNumberedMarker(number: Int): android.graphics.drawable.Drawable? {
        val drawable = ContextCompat.getDrawable(this, R.drawable.ic_marker)?.mutate()
        drawable?.let { DrawableCompat.setTint(it, Color.rgb(0, 120, 255)) }
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