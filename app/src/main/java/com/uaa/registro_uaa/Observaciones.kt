package com.example.tuapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.uaa.registro_uaa.R
import com.uaa.registro_uaa.MapaManzanaActivity
import com.uaa.registro_uaa.GeocodingUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint

class Observaciones : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private var idDocumento: String? = null

    // Variables para el mapa
    private var manzanaTrazada = false
    private lateinit var tvManzanaStatus: TextView

    companion object {
        const val REQUEST_CODE_MAPA = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tarjeta_salud_familiar7)

        db = FirebaseFirestore.getInstance()

        // Recuperar idDocumento de la actividad anterior
        idDocumento = intent.getStringExtra("idDocumento")
        if (idDocumento == null) {
            Toast.makeText(this, "No se recibió el ID del documento", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // === Referencias a los campos ===
        val encuestadores = findViewById<EditText>(R.id.encuestadores)
        val docentes = findViewById<EditText>(R.id.docentes)
        val grupo = findViewById<EditText>(R.id.grupo)
        val observaciones = findViewById<EditText>(R.id.observaciones)
        val vulnerabilidad = findViewById<EditText>(R.id.vulnerabilidad)
        val educSalud = findViewById<EditText>(R.id.educ_salud)
        val manzanaNum = findViewById<EditText>(R.id.manzana_num)
        val btnGuardar = findViewById<Button>(R.id.btnGuardarSeccion6)

        // NUEVO: Referencias al mapa
        val btnTrazarMapa = findViewById<Button>(R.id.btnTrazarMapa)
        tvManzanaStatus = findViewById(R.id.tvManzanaStatus)

        // === NUEVO: Acción del botón Trazar Mapa ===
        btnTrazarMapa.setOnClickListener {
            abrirMapaParaManzana(manzanaNum)
        }

        // === Acción del botón Guardar ===
        btnGuardar.setOnClickListener {
            guardarDatos(
                encuestadores, docentes, grupo, observaciones,
                vulnerabilidad, educSalud, manzanaNum
            )
        }
    }

    // NUEVA FUNCIÓN: Abrir mapa para trazar manzana
    private fun abrirMapaParaManzana(manzanaNumInput: EditText) {
        val manzanaNum = manzanaNumInput.text.toString()

        if (manzanaNum.isEmpty()) {
            Toast.makeText(
                this,
                "Por favor ingrese el número de manzana primero",
                Toast.LENGTH_SHORT
            ).show()
            manzanaNumInput.requestFocus()
            return
        }

        // Abrir MapaManzanaActivity
        val intent = Intent(this, MapaManzanaActivity::class.java)
        intent.putExtra("MANZANA_NUM", manzanaNum.toInt())
        intent.putExtra("ID_DOCUMENTO", idDocumento)
        startActivityForResult(intent, REQUEST_CODE_MAPA)
    }

    // NUEVA FUNCIÓN: Recibir resultado del mapa
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_MAPA && resultCode == RESULT_OK) {
            manzanaTrazada = true

            // Obtener datos adicionales si los pasó el mapa
            val numPuntos = data?.getIntExtra("NUM_PUNTOS", 0) ?: 0
            val centroLat = data?.getDoubleExtra("CENTRO_LAT", 0.0) ?: 0.0
            val centroLon = data?.getDoubleExtra("CENTRO_LON", 0.0) ?: 0.0

            // Actualizar UI con información clara
            tvManzanaStatus.text = buildString {
                append("✓ Manzana trazada\n")
                append("📍 $numPuntos esquinas marcadas\n")
                append("🗺️ Centro: ${String.format("%.5f", centroLat)}, ${String.format("%.5f", centroLon)}")
            }
            tvManzanaStatus.setTextColor(Color.parseColor("#4CAF50"))

            // Opcional: Obtener dirección
            if (centroLat != 0.0 && centroLon != 0.0) {
                obtenerDireccionParaMostrar(centroLat, centroLon)
            }

            Toast.makeText(
                this,
                "✓ Manzana registrada en el mapa con $numPuntos puntos",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // Nueva función para obtener dirección
    private fun obtenerDireccionParaMostrar(lat: Double, lon: Double) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                // Crear GeoPoint con las coordenadas
                val geoPoint = GeoPoint(lat, lon)

                // ✅ CORRECTO: Solo pasa el GeoPoint
                val direccion = GeocodingUtils.obtenerDireccionSimplificada(geoPoint)

                // Actualizar UI con la dirección
                tvManzanaStatus.text = buildString {
                    append("✓ Manzana trazada\n")
                    append("📫 $direccion")
                }
            } catch (e: Exception) {
                // Mantener info sin dirección si falla
                // No hacer nada, la info básica ya está mostrada
            }
        }
    }

    // FUNCIÓN MODIFICADA: Guardar datos
    private fun guardarDatos(
        encuestadores: EditText,
        docentes: EditText,
        grupo: EditText,
        observaciones: EditText,
        vulnerabilidad: EditText,
        educSalud: EditText,
        manzanaNum: EditText
    ) {
        // Validar que se haya trazado la manzana (opcional)
        if (!manzanaTrazada) {
            Toast.makeText(
                this,
                "⚠️ Advertencia: No se ha trazado la manzana en el mapa",
                Toast.LENGTH_LONG
            ).show()
            // Puedes decidir si permitir guardar o no
            // return // Descomentar para hacer obligatorio el trazo
        }

        // Datos del formulario
        val datosCierre = hashMapOf(
            "Encuestadores" to encuestadores.text.toString(),
            "Docentes" to docentes.text.toString(),
            "Grupo" to grupo.text.toString(),
            "Observaciones" to observaciones.text.toString(),
            "Vulnerabilidad familiar" to vulnerabilidad.text.toString(),
            "Educación para la salud impartida" to educSalud.text.toString(),
            "Número de manzana" to manzanaNum.text.toString(),
            "Manzana trazada en mapa" to manzanaTrazada
        )

        // Actualizar el documento existente
        idDocumento?.let { docId ->
            db.collection("Tarjeta_Salud")
                .document(docId)
                .update("cierre_encuesta", datosCierre)
                .addOnSuccessListener {
                    Toast.makeText(
                        this,
                        "✓ Datos actualizados exitosamente",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Opcional: Regresar o ir a otra actividad
                    // finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        this,
                        "Error al actualizar: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }
}