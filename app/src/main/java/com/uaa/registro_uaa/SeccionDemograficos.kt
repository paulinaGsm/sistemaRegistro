package com.example.tuapp

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.uaa.registro_uaa.R

class SeccionDemograficos : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    private lateinit var tableDemograficos: TableLayout
    private lateinit var tableVacunasPediatricos: TableLayout
    private lateinit var tableVacunasAdultos: TableLayout
    private lateinit var tableDetecciones: TableLayout

    private lateinit var btnAddIntegrante1: Button
    private lateinit var btnAddIntegrante2: Button
    private lateinit var btnAddIntegrante3: Button
    private lateinit var btnAddIntegrante4: Button
    private lateinit var btnGuardarSeccion5: Button

    // ✅ Definir los nombres de campos como constantes de clase
    private val camposDemograficos = listOf("Nombre", "Edad", "Sexo", "Parentesco", "Escolaridad", "Estado civil", "Ocupación")

    private val camposVacunasPediatricos = listOf("BCG", "Hepatitis B", "Hexa 1", "Hexa 2", "Hexa 3", "Hexa 4",
        "Rotavirus 1", "Rotavirus 2", "Neumo 1", "Neumo 2", "Neumo 3", "Influenza 1", "Influenza 2",
        "DPT", "SRP 1", "SRP 2", "COVID-19", "Otras", "ESQUEMA PEDIATRICO")

    private val camposVacunasAdultos = listOf("VPH", "TD 1", "TD 2", "TD 3", "TD Refuerzo", "S y r",
        "Hep B", "Influenza", "N-13V", "N-23V", "COVID-19", "Otras", "ESQUEMA ADOL-ADUL")

    private val camposDetecciones = listOf("Cancer C-U", "VPH Det", "Cáncer Mama", "Cáncer Próst",
        "Diabetes", "Hipertensión", "Osteoporosis", "Dislipidemias", "Hipotiroid C", "A.Visual",
        "A.Auditiva", "Peso (kg)", "Talla (cm)", "IMC")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tarjeta_salud_familiar5)

        db = FirebaseFirestore.getInstance()

        // Obtener idDocumento de la actividad anterior
        val idDocumento = intent.getStringExtra("idDocumento")
        if (idDocumento == null) {
            Toast.makeText(this, "No se recibió el ID del documento", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Vincular tablas
        tableDemograficos = findViewById(R.id.tableDemograficos)
        tableVacunasPediatricos = findViewById(R.id.tableVacunasPediatricos)
        tableVacunasAdultos = findViewById(R.id.tablevacunadoladul)
        tableDetecciones = findViewById(R.id.tableDetecciones)

        // Vincular botones
        btnAddIntegrante1 = findViewById(R.id.btnAddIntegrante1)
        btnAddIntegrante2 = findViewById(R.id.btnAddIntegrante2)
        btnAddIntegrante3 = findViewById(R.id.btnAddIntegrante3)
        btnAddIntegrante4 = findViewById(R.id.btnAddIntegrante4)
        btnGuardarSeccion5 = findViewById(R.id.btnGuardarSeccion5)

        // Listeners de botones para añadir filas
        btnAddIntegrante1.setOnClickListener { agregarFilaDemograficos() }
        btnAddIntegrante2.setOnClickListener { agregarFilaVacunasPediatricos() }
        btnAddIntegrante3.setOnClickListener { agregarFilaVacunasAdultos() }
        btnAddIntegrante4.setOnClickListener { agregarFilaDetecciones() }

        // Guardar todo en Firestore
        btnGuardarSeccion5.setOnClickListener { guardarDatosFirebase(idDocumento) }
    }

    private fun agregarFilaDemograficos() {
        val nuevaFila = TableRow(this)
        val numero = tableDemograficos.childCount

        val txtNumero = TextView(this).apply {
            text = numero.toString()
            setPadding(8, 8, 8, 8)
            gravity = Gravity.CENTER
        }
        nuevaFila.addView(txtNumero)

        for (campo in camposDemograficos) {
            val editText = EditText(this).apply {
                hint = campo
                setPadding(8, 8, 8, 8)
            }
            nuevaFila.addView(editText)
        }

        tableDemograficos.addView(nuevaFila)
    }

    private fun agregarFilaVacunasPediatricos() {
        val nuevaFila = TableRow(this)
        val numero = tableVacunasPediatricos.childCount

        val txtNumero = TextView(this).apply {
            text = numero.toString()
            setPadding(8, 8, 8, 8)
            gravity = Gravity.CENTER
        }
        nuevaFila.addView(txtNumero)

        for (campo in camposVacunasPediatricos) {
            val editText = EditText(this).apply {
                hint = campo
                setPadding(8, 8, 8, 8)
            }
            nuevaFila.addView(editText)
        }

        tableVacunasPediatricos.addView(nuevaFila)
    }

    private fun agregarFilaVacunasAdultos() {
        val nuevaFila = TableRow(this)
        val numero = tableVacunasAdultos.childCount

        val txtNumero = TextView(this).apply {
            text = numero.toString()
            setPadding(8, 8, 8, 8)
            gravity = Gravity.CENTER
        }
        nuevaFila.addView(txtNumero)

        for (campo in camposVacunasAdultos) {
            val editText = EditText(this).apply {
                hint = campo
                setPadding(8, 8, 8, 8)
            }
            nuevaFila.addView(editText)
        }

        tableVacunasAdultos.addView(nuevaFila)
    }

    private fun agregarFilaDetecciones() {
        val nuevaFila = TableRow(this)
        val numero = tableDetecciones.childCount

        val txtNumero = TextView(this).apply {
            text = numero.toString()
            setPadding(8, 8, 8, 8)
            gravity = Gravity.CENTER
        }
        nuevaFila.addView(txtNumero)

        for (campo in camposDetecciones) {
            val editText = EditText(this).apply {
                hint = campo
                setPadding(8, 8, 8, 8)
            }
            nuevaFila.addView(editText)
        }

        tableDetecciones.addView(nuevaFila)
    }

    private fun guardarDatosFirebase(idDocumento: String) {
        // ✅ Pasar los nombres de campos a cada función
        val datosDemograficos = obtenerDatosTabla(tableDemograficos, camposDemograficos)
        val datosVacunasPediatricos = obtenerDatosTabla(tableVacunasPediatricos, camposVacunasPediatricos)
        val datosVacunasAdultos = obtenerDatosTabla(tableVacunasAdultos, camposVacunasAdultos)
        val datosDetecciones = obtenerDatosTabla(tableDetecciones, camposDetecciones)

        // ✅ Solo incluir secciones que tengan datos
        val datos = hashMapOf<String, Any>()

        if (datosDemograficos.isNotEmpty()) {
            datos["demograficos"] = datosDemograficos
        }
        if (datosVacunasPediatricos.isNotEmpty()) {
            datos["vacunas_pediatricos"] = datosVacunasPediatricos
        }
        if (datosVacunasAdultos.isNotEmpty()) {
            datos["vacunas_adultos"] = datosVacunasAdultos
        }
        if (datosDetecciones.isNotEmpty()) {
            datos["detecciones"] = datosDetecciones
        }

        // Si no hay datos en ninguna tabla, informar al usuario
        if (datos.isEmpty()) {
            Toast.makeText(this, "No hay datos para guardar", Toast.LENGTH_SHORT).show()
            return
        }

        // Actualizar el documento existente en Firestore
        db.collection("Tarjeta_Salud")
            .document(idDocumento)
            .update("Datos_familiares", datos)
            .addOnSuccessListener {
                Toast.makeText(this, "Datos actualizados exitosamente", Toast.LENGTH_SHORT).show()

                val nombres = arrayListOf<String>()
                val inputNombre1 = findViewById<EditText>(R.id.inputNombre1)
                val inputNombre2 = findViewById<EditText>(R.id.inputNombre2)
                val inputNombre3 = findViewById<EditText>(R.id.inputNombre3)
                val inputNombre4 = findViewById<EditText>(R.id.inputNombre4)

                // ✅ Solo agregar nombres que NO estén vacíos
                val nombre1 = inputNombre1.text.toString().trim()
                val nombre2 = inputNombre2.text.toString().trim()
                val nombre3 = inputNombre3.text.toString().trim()
                val nombre4 = inputNombre4.text.toString().trim()

                if (nombre1.isNotEmpty()) nombres.add(nombre1)
                if (nombre2.isNotEmpty()) nombres.add(nombre2)
                if (nombre3.isNotEmpty()) nombres.add(nombre3)
                if (nombre4.isNotEmpty()) nombres.add(nombre4)

                val intent = Intent(this, Deportes::class.java)
                intent.putStringArrayListExtra("nombresUsuarios", nombres)
                intent.putExtra("idDocumento", idDocumento)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al actualizar: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // ✅ FUNCIÓN ACTUALIZADA: Ahora recibe la lista de nombres de campos
    private fun obtenerDatosTabla(tabla: TableLayout, nombresCampos: List<String>): List<Map<String, String>> {
        val lista = mutableListOf<Map<String, String>>()

        for (i in 1 until tabla.childCount) { // Saltamos encabezado
            val fila = tabla.getChildAt(i) as TableRow
            val filaDatos = mutableMapOf<String, String>()
            var indiceEditText = 0 // Para trackear el índice en la lista de nombres

            for (j in 0 until fila.childCount) {
                val view = fila.getChildAt(j)

                when (view) {
                    is EditText -> {
                        val valor = view.text.toString().trim()

                        // ✅ Usar el nombre del campo según el índice
                        val nombreCampo = if (indiceEditText < nombresCampos.size) {
                            nombresCampos[indiceEditText]
                        } else {
                            "campo${indiceEditText}"
                        }

                        indiceEditText++

                        // Solo agregar si NO está vacío
                        if (valor.isNotEmpty()) {
                            filaDatos[nombreCampo] = valor
                        }
                    }

                    is TextView -> {
                        val valor = view.text.toString().trim()
                        // Solo agregar si NO está vacío
                        if (valor.isNotEmpty()) {
                            filaDatos["Número"] = valor
                        }
                    }
                }
            }

            // Solo agregamos la fila si tiene al menos UN dato
            if (filaDatos.isNotEmpty()) {
                lista.add(filaDatos)
            }
        }

        return lista
    }
}