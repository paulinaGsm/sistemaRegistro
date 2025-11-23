package com.example.tuapp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.uaa.registro_uaa.R

class DatosAdicionalesActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    // Campos
    private lateinit var etTxtMortalidad: EditText
    private lateinit var tableMortalidad: TableLayout
    private lateinit var btnAddMortalidadRow: Button

    private lateinit var etTxtMorbilidad: EditText
    private lateinit var tableMorbilidad: TableLayout
    private lateinit var btnAddMorbilidadRow: Button

    private lateinit var btnGuardarSiguiente: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tarjeta_salud_familiar3)

        // Inicializar vistas
        etTxtMortalidad = findViewById(R.id.et_txtMortalidad)
        tableMortalidad = findViewById(R.id.table_mortalidad)
        btnAddMortalidadRow = findViewById(R.id.btnAddMortalidadRow)

        etTxtMorbilidad = findViewById(R.id.et_txtMorbilidad)
        tableMorbilidad = findViewById(R.id.table_morbilidad)
        btnAddMorbilidadRow = findViewById(R.id.btnAddMorbilidadRow)

        btnGuardarSiguiente = findViewById(R.id.btnGuardarSeccion3)

        // Botones para añadir filas dinámicamente
        btnAddMortalidadRow.setOnClickListener { agregarFila(tableMortalidad, 3) }
        btnAddMorbilidadRow.setOnClickListener { agregarFila(tableMorbilidad, 5) }

        // Obtener el idDocumento de la actividad anterior
        val idDocumento = intent.getStringExtra("idDocumento")
        if (idDocumento == null) {
            Toast.makeText(this, "No se recibió el ID del documento", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Guardar y Siguiente
        btnGuardarSiguiente.setOnClickListener { guardarDatos(idDocumento) }
    }

    private fun agregarFila(tabla: TableLayout, columnas: Int) {
        val fila = TableRow(this)
        for (i in 0 until columnas) {
            val editText = EditText(this).apply {
                layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
                gravity = android.view.Gravity.CENTER
                setPadding(8, 8, 8, 8)
                setSingleLine()
            }
            fila.addView(editText)
        }
        tabla.addView(fila)
    }

    private fun obtenerDatosTabla(tabla: TableLayout, columnas: List<String>): List<Map<String, String>> {
        val lista = mutableListOf<Map<String, String>>()

        for (i in 1 until tabla.childCount) {  // saltamos el encabezado
            val fila = tabla.getChildAt(i) as? TableRow ?: continue
            val datosFila = mutableMapOf<String, String>()
            var filaTieneDatos = false

            for (j in columnas.indices) {
                val celda = fila.getChildAt(j) as? EditText
                val valor = celda?.text?.toString()?.trim().orEmpty()

                if (valor.isNotEmpty()) {   // ⬅️ solo guardar columnas con datos
                    filaTieneDatos = true
                    datosFila[columnas[j]] = valor
                }
            }

            if (filaTieneDatos) {  // ⬅️ solo agregar la fila si tiene algo capturado
                lista.add(datosFila)
            }
        }

        return lista
    }


    private fun guardarDatos(idDocumento: String) {
        val mortalidadInfo = etTxtMortalidad.text.toString().trim()
        val morbilidadInfo = etTxtMorbilidad.text.toString().trim()

        val mortalidadLista = obtenerDatosTabla(tableMortalidad, listOf("Sexo", "Edad", "Causa"))
        val morbilidadLista = obtenerDatosTabla(tableMorbilidad, listOf("Sexo", "Edad", "Causa", "DX", "TX"))

        val datosCondicionantes = hashMapOf(
            "Mortalidad" to hashMapOf(
                "Descripcion" to mortalidadInfo,
                "Casos" to mortalidadLista
            ),
            "Morbilidad" to hashMapOf(
                "Descripcion" to morbilidadInfo,
                "Casos" to morbilidadLista
            )
        )

        // Actualizar el documento existente en Firestore
        db.collection("Tarjeta_Salud")
            .document(idDocumento)
            .update("Datos_Condicionantes", datosCondicionantes)
            .addOnSuccessListener {
                Toast.makeText(this, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, Alimentacion::class.java)
                intent.putExtra("idDocumento", idDocumento)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al actualizar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
