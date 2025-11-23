package com.uaa.registro_uaa

import android.content.Context
import android.location.Address
import android.location.Geocoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.osmdroid.util.GeoPoint
import java.net.URL
import java.util.*

object GeocodingUtils {

    /**
     * Opción 1: Usar Geocoder de Android (Requiere Google Play Services)
     */
    fun obtenerDireccionConGeocoder(
        context: Context,
        geoPoint: GeoPoint,
        callback: (String) -> Unit
    ) {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses: List<Address>? = geocoder.getFromLocation(
                geoPoint.latitude,
                geoPoint.longitude,
                1
            )

            if (addresses != null && addresses.isNotEmpty()) {
                val address = addresses[0]
                val direccion = buildString {
                    address.thoroughfare?.let { append("$it ") } // Calle
                    address.subThoroughfare?.let { append("$it, ") } // Número
                    address.locality?.let { append("$it, ") } // Ciudad
                    address.adminArea?.let { append(it) } // Estado
                }
                callback(direccion.ifEmpty { "Dirección no disponible" })
            } else {
                callback("Dirección no encontrada")
            }
        } catch (e: Exception) {
            callback("Error al obtener dirección")
        }
    }

    /**
     * Opción 2: Usar Nominatim API (Gratuita, sin API Key)
     */
    suspend fun obtenerDireccionConNominatim(geoPoint: GeoPoint): String {
        return withContext(Dispatchers.IO) {
            try {
                val url = "https://nominatim.openstreetmap.org/reverse?" +
                        "format=json&" +
                        "lat=${geoPoint.latitude}&" +
                        "lon=${geoPoint.longitude}&" +
                        "zoom=18&" +
                        "addressdetails=1"

                val response = URL(url).readText()
                val json = JSONObject(response)

                if (json.has("display_name")) {
                    json.getString("display_name")
                } else {
                    "Dirección no disponible"
                }
            } catch (e: Exception) {
                "Error: ${e.message}"
            }
        }
    }

    /**
     * Opción 3: Formato simplificado y legible (SOLO GeoPoint)
     */
    suspend fun obtenerDireccionSimplificada(geoPoint: GeoPoint): String {
        return withContext(Dispatchers.IO) {
            try {
                val url = "https://nominatim.openstreetmap.org/reverse?" +
                        "format=json&" +
                        "lat=${geoPoint.latitude}&" +
                        "lon=${geoPoint.longitude}&" +
                        "zoom=18&" +
                        "addressdetails=1"

                val response = URL(url).readText()
                val json = JSONObject(response)

                if (json.has("address")) {
                    val address = json.getJSONObject("address")
                    buildString {
                        address.optString("road")?.let {
                            if (it.isNotEmpty()) append("$it ")
                        }
                        address.optString("house_number")?.let {
                            if (it.isNotEmpty()) append("$it, ")
                        }
                        address.optString("suburb")?.let {
                            if (it.isNotEmpty()) append("$it, ")
                        }
                        address.optString("city")?.let {
                            if (it.isNotEmpty()) append(it)
                        }
                    }.ifEmpty { "Ubicación sin nombre de calle" }
                } else {
                    "Zona sin dirección registrada"
                }
            } catch (e: Exception) {
                "Ubicación registrada"
            }
        }
    }

    /**
     * Formato corto para mostrar en marcadores
     */
    fun formatearDireccionCorta(direccionCompleta: String): String {
        // Toma solo las primeras 3 partes de la dirección
        val partes = direccionCompleta.split(",")
        return if (partes.size > 3) {
            "${partes[0]}, ${partes[1]}, ${partes[2]}"
        } else {
            direccionCompleta
        }
    }

    /**
     * Crear descripción amigable de la ubicación
     */
    fun crearDescripcionAmigable(
        manzanaNum: Int,
        numPuntos: Int,
        direccion: String? = null
    ): String {
        return buildString {
            append("🏘️ Manzana #$manzanaNum\n")
            append("📍 $numPuntos puntos marcados\n")
            direccion?.let {
                append("📫 $it")
            }
        }
    }
}