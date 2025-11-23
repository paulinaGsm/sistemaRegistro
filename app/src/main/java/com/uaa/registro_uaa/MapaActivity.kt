package com.uaa.registro_uaa

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
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
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class MapaActivity : AppCompatActivity() {

    private lateinit var map: MapView
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // ubicación GPS
    private lateinit var myLocationOverlay: MyLocationNewOverlay
    private var myCurrentLocation: GeoPoint? = null

    // Marcador verde temporal
    private var myPositionMarker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // Configuración básica OSMDroid
        Configuration.getInstance().load(
            applicationContext,
            getSharedPreferences("osmdroid", MODE_PRIVATE)
        )

        setContentView(R.layout.activity_mapa)

        map = findViewById(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)

        val controller = map.controller
        controller.setZoom(15.0)
        controller.setCenter(GeoPoint(21.8853, -102.2916))

        // ========== ACTIVAR MI UBICACIÓN (GPS) ==========
        myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this), map)
        myLocationOverlay.enableMyLocation()
        myLocationOverlay.enableFollowLocation()
        map.overlays.add(myLocationOverlay)

        // obtener ubicación actual
        myLocationOverlay.runOnFirstFix {
            runOnUiThread {
                myCurrentLocation = myLocationOverlay.myLocation
            }
        }

        // ========== BOTÓN MI UBICACIÓN ==========
        val btnMyLocation = findViewById<ImageButton>(R.id.btnMyLocation)

        btnMyLocation.setOnClickListener {
            if (myCurrentLocation != null) {
                map.controller.animateTo(myCurrentLocation)
                showGreenMarker(myCurrentLocation!!)
            } else {
                Toast.makeText(this, "Obteniendo ubicación...", Toast.LENGTH_SHORT).show()
            }
        }

        // cargar todas las ubicaciones
        loadAllLocations()

        // ========== TOQUE PARA COLOCAR MARCADOR ==========
        val mapEventsReceiver = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                p?.let { addMyMarker(it) }
                return true
            }

            override fun longPressHelper(p: GeoPoint?): Boolean {
                return false
            }
        }

        val eventsOverlay = MapEventsOverlay(mapEventsReceiver)
        map.overlays.add(eventsOverlay)
    }

    // ==================== MIS MARCADORES ====================

    private fun addMyMarker(point: GeoPoint) {
        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        val marker = Marker(map)
        marker.position = point
        marker.title = "Mi marcador"
        marker.snippet = "${user.email}\nLat: ${point.latitude}\nLon: ${point.longitude}"
        marker.icon = getColoredMarker(Color.RED)

        map.overlays.add(marker)
        map.invalidate()

        saveLocationToFirestore(point.latitude, point.longitude)
    }

    private fun saveLocationToFirestore(lat: Double, lon: Double) {
        val user = auth.currentUser ?: return

        val locationData = hashMapOf(
            "Latitud" to lat,
            "Longitud" to lon,
            "timestamp" to System.currentTimeMillis(),
            "Usuario_Email" to user.email,
            "userId" to user.uid
        )

        db.collection("usuarios")
            .document(user.uid)
            .collection("locations")
            .add(locationData)
            .addOnSuccessListener {
                Toast.makeText(this, "✓ Ubicación guardada", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // ==================== CARGAR TODO ====================

    private fun loadAllLocations() {
        val currentUserId = auth.currentUser?.uid

        db.collection("usuarios")
            .get()
            .addOnSuccessListener { usersSnapshot ->

                map.overlays.removeAll { it is Marker }

                for (userDoc in usersSnapshot.documents) {
                    val uid = userDoc.id
                    val locationsRef = db.collection("usuarios").document(uid).collection("locations")

                    locationsRef.get().addOnSuccessListener { locSnapshot ->

                        for (loc in locSnapshot.documents) {
                            val lat = loc.getDouble("Latitud")
                            val lon = loc.getDouble("Longitud")
                            val email = loc.getString("Usuario_Email") ?: "Desconocido"

                            if (lat != null && lon != null) {
                                val marker = Marker(map)
                                marker.position = GeoPoint(lat, lon)

                                if (uid == currentUserId) {
                                    marker.title = "Mi marcador"
                                    marker.icon = getColoredMarker(Color.RED)
                                } else {
                                    marker.title = email
                                    marker.icon = getColoredMarker(Color.BLUE)
                                }

                                map.overlays.add(marker)
                            }
                        }

                        map.invalidate()
                    }
                }
            }
    }

    // ========== MARCADOR VERDE GPS ==========
    private fun showGreenMarker(point: GeoPoint) {

        if (myPositionMarker != null) {
            map.overlays.remove(myPositionMarker)
        }

        myPositionMarker = Marker(map)
        myPositionMarker!!.position = point
        myPositionMarker!!.title = "Mi ubicación actual"
        myPositionMarker!!.icon = getGreenMarker()
        myPositionMarker!!.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

        map.overlays.add(myPositionMarker)
        map.invalidate()
    }

    // Colores
    private fun getColoredMarker(color: Int): Drawable? {
        val drawable = ContextCompat.getDrawable(this, R.drawable.ic_marker)?.mutate()
        drawable?.let { DrawableCompat.setTint(it, color) }
        return drawable
    }

    private fun getGreenMarker(): Drawable? {
        val drawable = ContextCompat.getDrawable(this, R.drawable.ic_marker)?.mutate()
        drawable?.let { DrawableCompat.setTint(it, Color.GREEN) }
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
