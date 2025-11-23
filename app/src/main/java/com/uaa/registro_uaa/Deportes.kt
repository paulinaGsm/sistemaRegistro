package com.example.tuapp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.uaa.registro_uaa.R

class Deportes : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    // ✅ Definir los nombres de las columnas para cada tabla
    private val columnasDeportes = listOf("Número", "Nombre", "Deporte", "Frecuencia", "Duración")

    private val columnasATE = listOf("Número", "Nombre", "Alteración", "Tratamiento", "Fecha inicio", "Estado")

    private val columnasAdicciones = listOf("Número", "Nombre", "Tipo adicción", "Frecuencia", "Años consumo", "En tratamiento")

    private val columnasCD = listOf("Número", "Nombre", "IMC", "Estado nutricional", "Última desparasitación", "Observaciones")

    private val columnasEmbarazo = listOf("Número", "Nombre", "Embarazada", "Semanas gestación", "Fecha probable parto", "Control prenatal", "Observaciones")

    private val columnasLRMITLTL = listOf("Número", "Nombre", "Lactancia", "Referencias médicas", "ITS", "Método planificación", "Violencia", "Observaciones")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tarjeta_salud_familiar6)

        db = FirebaseFirestore.getInstance()

        // Recuperar idDocumento y lista de nombres de la sección anterior
        val idDocumento = intent.getStringExtra("idDocumento")
        if (idDocumento == null) {
            Toast.makeText(this, "No se recibió el ID del documento", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val nombresUsuarios = intent.getStringArrayListExtra("nombresUsuarios") ?: arrayListOf()

        // Referencias a las tablas
        val tablaDeportes = findViewById<TableLayout>(R.id.tableDeportes)
        val tablaATE = findViewById<TableLayout>(R.id.tableATE)
        val tablaAdicciones = findViewById<TableLayout>(R.id.tableAdicciones)
        val tablaCD = findViewById<TableLayout>(R.id.tableCD)
        val tablaEmbarazo = findViewById<TableLayout>(R.id.tableEmbarazo)
        val tablaLRMITLTL = findViewById<TableLayout>(R.id.tableLRMITLTL)

        // Rellenar todas las tablas con los nombres
        rellenarNombresEnTabla(tablaDeportes, nombresUsuarios)
        //rellenarNombresEnTabla(tablaATE, nombresUsuarios)
        //rellenarNombresEnTabla(tablaAdicciones, nombresUsuarios)
       // rellenarNombresEnTabla(tablaCD, nombresUsuarios)
       // rellenarNombresEnTabla(tablaEmbarazo, nombresUsuarios)
       // rellenarNombresEnTabla(tablaLRMITLTL, nombresUsuarios)

        // Botón guardar
        val btnGuardar = findViewById<Button>(R.id.btnGuardarSeccion6)
        btnGuardar.setOnClickListener {

            if (nombresUsuarios.isEmpty()) {
                Toast.makeText(this, "No hay nombres para guardar.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // ✅ Pasar los nombres de columnas a cada función
            val datosDeportes = obtenerDatosDeTabla(tablaDeportes, columnasDeportes)
            val datosATE = obtenerDatosDeTabla(tablaATE, columnasATE)
            val datosAdicciones = obtenerDatosDeTabla(tablaAdicciones, columnasAdicciones)
            val datosCD = obtenerDatosDeTabla(tablaCD, columnasCD)
            val datosEmbarazo = obtenerDatosDeTabla(tablaEmbarazo, columnasEmbarazo)
            val datosLRMITLTL = obtenerDatosDeTabla(tablaLRMITLTL, columnasLRMITLTL)

            // ✅ Solo incluir secciones con datos
            val datos = hashMapOf<String, Any>()

            if (datosDeportes.isNotEmpty()) {
                datos["Deporte"] = datosDeportes
            }
            if (datosATE.isNotEmpty()) {
                datos["Alteraciones de la salud"] = datosATE
            }
            if (datosAdicciones.isNotEmpty()) {
                datos["Adicciones"] = datosAdicciones
            }
            if (datosCD.isNotEmpty()) {
                datos["Control nutricional y desparasitación"] = datosCD
            }
            if (datosEmbarazo.isNotEmpty()) {
                datos["Embarazo"] = datosEmbarazo
            }
            if (datosLRMITLTL.isNotEmpty()) {
                datos["Lactancia, referencias, ITS, planificación, violencia"] = datosLRMITLTL
            }

            // Si no hay datos, informar
            if (datos.isEmpty()) {
                Toast.makeText(this, "No hay datos para guardar", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Actualizar el documento existente
            db.collection("Tarjeta_Salud")
                .document(idDocumento)
                .update("estilos_vida", datos)
                .addOnSuccessListener {
                    Toast.makeText(this, "Datos actualizados exitosamente", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, Observaciones::class.java)
                    intent.putExtra("idDocumento", idDocumento)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al actualizar: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun rellenarNombresEnTabla(tabla: TableLayout, nombres: List<String>) {
        val filasExistentes = tabla.childCount - 1 // Resta el encabezado

        // Recorre las filas existentes
        for (i in 1..filasExistentes) {
            val fila = tabla.getChildAt(i)
            if (fila is TableRow && (i - 1) < nombres.size) {
                val primerCampo = fila.getChildAt(1)
                if (primerCampo is EditText) {
                    primerCampo.setText(nombres[i - 1])
                }
            }
        }

        // Si hay más nombres que filas → crear filas nuevas
        if (nombres.size > filasExistentes) {
            val columnas = (tabla.getChildAt(1) as TableRow).childCount
            for (i in filasExistentes until nombres.size) {
                val nuevaFila = TableRow(this)

                val txtNum = TextView(this)
                txtNum.text = (i + 1).toString()
                txtNum.setPadding(8, 8, 8, 8)

                val editNombre = EditText(this)
                editNombre.setText(nombres[i])
                editNombre.setPadding(8, 8, 8, 8)

                nuevaFila.addView(txtNum)
                nuevaFila.addView(editNombre)

                for (j in 2 until columnas) {
                    val edit = EditText(this)
                    edit.setPadding(8, 8, 8, 8)
                    nuevaFila.addView(edit)
                }

                tabla.addView(nuevaFila)
            }
        }
    }

    // ✅ FUNCIÓN ACTUALIZADA: Ahora recibe los nombres de las columnas
    private fun obtenerDatosDeTabla(tabla: TableLayout, nombresColumnas: List<String>): List<Map<String, String>> {
        val lista = mutableListOf<Map<String, String>>()

        for (i in 1 until tabla.childCount) { // saltar encabezado
            val fila = tabla.getChildAt(i) as? TableRow ?: continue
            val filaDatos = mutableMapOf<String, String>()

            for (j in 0 until fila.childCount) {
                val vista = fila.getChildAt(j)
                val valor = when (vista) {
                    is EditText -> vista.text.toString().trim()
                    is TextView -> vista.text.toString().trim()
                    else -> ""
                }

                // ✅ Usar el nombre de la columna según el índice
                val nombreColumna = if (j < nombresColumnas.size) {
                    nombresColumnas[j]
                } else {
                    "columna$j"
                }

                // Solo agregar si NO está vacío
                if (valor.isNotEmpty()) {
                    filaDatos[nombreColumna] = valor
                }
            }

            // Solo agrega la fila si tiene al menos un dato
            if (filaDatos.isNotEmpty()) {
                lista.add(filaDatos)
            }
        }

        return lista
    }
}