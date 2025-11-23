package com.uaa.registro_uaa

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import com.itextpdf.text.pdf.draw.LineSeparator
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

class DescargarReporteActivity : AppCompatActivity() {

    private lateinit var etIdEstudiante: EditText
    private lateinit var btnBuscar: Button
    private lateinit var rvEstudiantes: RecyclerView

    private val db = FirebaseFirestore.getInstance()
    private val listaDocumentos = mutableListOf<DocumentSnapshot>()
    private lateinit var adapter: EstudianteAdapter
    private lateinit var idAlumnoSesion: String

    // ✅ Definir fuentes y colores elegantes
    private val colorPrimario = BaseColor(46, 125, 50) // Verde profesional
    private val colorSecundario = BaseColor(100, 181, 246) // Azul suave
    private val colorGris = BaseColor(117, 117, 117) // Gris para subtítulos
    private val colorFondo = BaseColor(245, 245, 245) // Gris muy claro para tablas

    private val fuenteTitulo = Font(Font.FontFamily.HELVETICA, 20f, Font.BOLD, colorPrimario)
    private val fuenteSubtitulo = Font(Font.FontFamily.HELVETICA, 14f, Font.BOLD, colorSecundario)
    private val fuenteTexto = Font(Font.FontFamily.HELVETICA, 10f, Font.NORMAL, BaseColor.BLACK)
    private val fuenteTextoBold = Font(Font.FontFamily.HELVETICA, 10f, Font.BOLD, BaseColor.BLACK)
    private val fuentePequena = Font(Font.FontFamily.HELVETICA, 8f, Font.ITALIC, colorGris)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_descargar_reporte)

        etIdEstudiante = findViewById(R.id.et_id_estudiante)
        btnBuscar = findViewById(R.id.btn_buscar)
        rvEstudiantes = findViewById(R.id.rvEstudiantes)

        val prefs = getSharedPreferences("usuario_prefs", Context.MODE_PRIVATE)
        idAlumnoSesion = prefs.getString("idEstudiante", "") ?: ""

        if (idAlumnoSesion.isEmpty()) {
            Toast.makeText(this, "No se pudo obtener tu ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        etIdEstudiante.setText(idAlumnoSesion)
        etIdEstudiante.isEnabled = false

        adapter = EstudianteAdapter(listaDocumentos) { doc ->
            val datos = doc.data ?: return@EstudianteAdapter
            val nombre = datos["Descriptivos de la persona"]?.let { (it as Map<*, *>)["Nombre"] } ?: "Sin nombre"
            val idEstudiante = datos["Descriptivos de la persona"]?.let { (it as Map<*, *>)["idEstudiante"] } ?: ""
            crearPDF(datos, "$nombre", idEstudiante.toString())
        }

        rvEstudiantes.layoutManager = LinearLayoutManager(this)
        rvEstudiantes.adapter = adapter

        btnBuscar.setOnClickListener {
            buscarEncuestas(idAlumnoSesion)
        }
    }

    private fun buscarEncuestas(idAlumno: String) {
        db.collection("guia-valoracion")
            .whereEqualTo("Descriptivos de la persona.idEstudiante", idAlumno)
            .get()
            .addOnSuccessListener { documents ->
                listaDocumentos.clear()

                if (!documents.isEmpty) {
                    listaDocumentos.addAll(documents.documents)
                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this, "No se encontraron encuestas para tu ID", Toast.LENGTH_SHORT).show()
                    adapter.notifyDataSetChanged()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al buscar encuestas", Toast.LENGTH_SHORT).show()
            }
    }

    private fun crearPDF(datos: Map<String, Any>, nombrePaciente: String, idEstudiante: String) {
        val documento = Document(PageSize.A4, 40f, 40f, 50f, 50f) // Márgenes profesionales
        val nombreArchivo = "GuiaValoracion_${nombrePaciente.replace(" ", "_")}_${System.currentTimeMillis()}.pdf"

        var outputStream: OutputStream? = null

        try {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, nombreArchivo)
                put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }

            val resolver = contentResolver
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            if (uri != null) {
                outputStream = resolver.openOutputStream(uri)
            } else {
                throw Exception("No se pudo crear la URI para el archivo.")
            }

            if (outputStream != null) {
                PdfWriter.getInstance(documento, outputStream)
                documento.open()

                // ✅ ENCABEZADO ELEGANTE
                agregarEncabezado(documento, nombrePaciente, idEstudiante)

                // ✅ LÍNEA SEPARADORA
                documento.add(Chunk(LineSeparator(1f, 100f, colorPrimario, Element.ALIGN_CENTER, -5f)))
                documento.add(Paragraph("\n"))

                // ✅ Procesar y mostrar datos organizados
                val datosFiltrados = limpiarDatosVacios(datos)

                datosFiltrados.forEach { (key, value) ->
                    when (value) {
                        is Map<*, *> -> {
                            // TODOS los Maps van a formato tabla
                            agregarSeccionConTabla(documento, key, value)
                        }
                        is List<*> -> {
                            // Procesar cada elemento de la lista
                            value.forEachIndexed { index, item ->
                                if (item is Map<*, *>) {
                                    val subtitulo = if (value.size > 1) "$key - Registro ${index + 1}" else key
                                    agregarSeccionConTabla(documento, subtitulo, item)
                                } else {
                                    val respuesta = formatearRespuesta(item)
                                    if (respuesta.isNotBlank()) {
                                        agregarSeccionSimple(documento, key, respuesta)
                                    }
                                }
                            }
                        }
                        else -> {
                            // Solo valores simples (strings, números) van a formato simple
                            val respuesta = formatearRespuesta(value)
                            if (respuesta.isNotBlank()) {
                                agregarSeccionSimple(documento, key, respuesta)
                            }
                        }
                    }
                }

                // ✅ PIE DE PÁGINA
                agregarPiePagina(documento)

                documento.close()
                outputStream.close()

                Toast.makeText(this, "✅ PDF generado exitosamente en Descargas", Toast.LENGTH_LONG).show()
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "❌ Error al generar PDF: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    // ✅ ENCABEZADO ELEGANTE CON LOGO Y DATOS
    private fun agregarEncabezado(documento: Document, nombrePaciente: String, idEstudiante: String) {
        // Título principal
        val titulo = Paragraph("GUIA DE SALUD FAMILIAR", fuenteTitulo)
        titulo.alignment = Element.ALIGN_CENTER
        titulo.spacingAfter = 10f
        documento.add(titulo)

        // Información del paciente en tabla
        val tablaInfo = PdfPTable(2)
        tablaInfo.widthPercentage = 100f
        tablaInfo.setWidths(floatArrayOf(1f, 2f))
        tablaInfo.spacingAfter = 15f

        // Paciente
        agregarCeldaInfo(tablaInfo, "Paciente:", nombrePaciente)

        // ID Estudiante
        agregarCeldaInfo(tablaInfo, "ID Estudiante:", idEstudiante)

        // Fecha de generación
        val fechaActual = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
        agregarCeldaInfo(tablaInfo, "Fecha generación:", fechaActual)

        documento.add(tablaInfo)
    }

    private fun agregarCeldaInfo(tabla: PdfPTable, label: String, valor: String) {
        // Celda de etiqueta
        val celdaLabel = PdfPCell(Phrase(label, fuenteTextoBold))
        celdaLabel.backgroundColor = colorFondo
        celdaLabel.border = Rectangle.NO_BORDER
        celdaLabel.paddingLeft = 10f
        celdaLabel.paddingTop = 5f
        celdaLabel.paddingBottom = 5f
        tabla.addCell(celdaLabel)

        // Celda de valor
        val celdaValor = PdfPCell(Phrase(valor, fuenteTexto))
        celdaValor.border = Rectangle.NO_BORDER
        celdaValor.paddingLeft = 10f
        celdaValor.paddingTop = 5f
        celdaValor.paddingBottom = 5f
        tabla.addCell(celdaValor)
    }

    // ✅ SECCIÓN CON TABLA ELEGANTE
    private fun agregarSeccionConTabla(documento: Document, titulo: String, value: Any) {
        // Título de sección
        val parrafoTitulo = Paragraph(titulo.uppercase(), fuenteSubtitulo)
        parrafoTitulo.spacingBefore = 25f
        parrafoTitulo.spacingAfter = 8f
        documento.add(parrafoTitulo)

        when (value) {
            is Map<*, *> -> {
                val tabla = PdfPTable(2)
                tabla.widthPercentage = 100f
                tabla.setWidths(floatArrayOf(1.5f, 2.5f))

                // Encabezado de tabla
                val fuenteBlanca = Font(Font.FontFamily.HELVETICA, 10f, Font.BOLD, BaseColor.WHITE)

                val celdaHeaderCampo = PdfPCell(Phrase("Campo", fuenteBlanca))
                celdaHeaderCampo.backgroundColor = colorPrimario
                celdaHeaderCampo.paddingTop = 8f
                celdaHeaderCampo.paddingBottom = 8f
                celdaHeaderCampo.horizontalAlignment = Element.ALIGN_CENTER
                tabla.addCell(celdaHeaderCampo)

                val celdaHeaderValor = PdfPCell(Phrase("Valor", fuenteBlanca))
                celdaHeaderValor.backgroundColor = colorPrimario
                celdaHeaderValor.paddingTop = 8f
                celdaHeaderValor.paddingBottom = 8f
                celdaHeaderValor.horizontalAlignment = Element.ALIGN_CENTER
                tabla.addCell(celdaHeaderValor)

                // Filas de datos
                value.entries
                    .filter {
                        it.key != null &&
                                it.value != null &&
                                it.value.toString().trim().isNotEmpty() &&
                                it.value.toString() != "null" &&
                                !it.key.toString().contains("columna", ignoreCase = true)
                    }
                    .forEach { (key, valor) ->
                        // Celda de campo
                        val celdaCampo = PdfPCell(Phrase(key.toString(), fuenteTextoBold))
                        celdaCampo.backgroundColor = colorFondo
                        celdaCampo.paddingLeft = 8f
                        celdaCampo.paddingTop = 6f
                        celdaCampo.paddingBottom = 6f
                        tabla.addCell(celdaCampo)

                        // Celda de valor
                        val celdaValor = PdfPCell(Phrase(valor.toString(), fuenteTexto))
                        celdaValor.paddingLeft = 8f
                        celdaValor.paddingTop = 6f
                        celdaValor.paddingBottom = 6f
                        tabla.addCell(celdaValor)
                    }

                tabla.spacingAfter = 10f
                documento.add(tabla)
            }

            is List<*> -> {
                value.forEach { item ->
                    if (item is Map<*, *>) {
                        agregarSeccionConTabla(documento, "Registro", item)
                    }
                }
            }
        }
    }

    // ✅ SECCIÓN SIMPLE CON VIÑETAS
    private fun agregarSeccionSimple(documento: Document, titulo: String, contenido: String) {
        val parrafoTitulo = Paragraph(titulo, fuenteSubtitulo)
        parrafoTitulo.spacingBefore = 12f
        parrafoTitulo.spacingAfter = 5f
        documento.add(parrafoTitulo)

        val parrafoContenido = Paragraph(contenido, fuenteTexto)
        parrafoContenido.indentationLeft = 20f
        parrafoContenido.spacingAfter = 8f
        documento.add(parrafoContenido)
    }

    // ✅ PIE DE PÁGINA
    private fun agregarPiePagina(documento: Document) {
        documento.add(Paragraph("\n\n"))
        documento.add(Chunk(LineSeparator(0.5f, 100f, colorGris, Element.ALIGN_CENTER, -5f)))

        val pie = Paragraph(
            "\nDocumento generado automáticamente por Sistema de Registro UAA\n" +
                    "Este documento contiene información confidencial",
            fuentePequena
        )
        pie.alignment = Element.ALIGN_CENTER
        documento.add(pie)
    }

    private fun formatearRespuesta(value: Any?): String {
        if (value == null) return ""

        return when (value) {
            is Map<*, *> -> {
                val contenido = value.entries
                    .filter {
                        it.key != null &&
                                it.value != null &&
                                it.value.toString().trim().isNotEmpty() &&
                                it.value.toString() != "null" &&
                                !it.key.toString().contains("columna", ignoreCase = true)
                    }
                    .joinToString("\n") { "  • ${it.key}: ${it.value}" }

                if (contenido.isEmpty()) "" else contenido
            }

            is List<*> -> {
                val contenido = value
                    .filterNotNull()
                    .map { it.toString().trim() }
                    .filter { it.isNotEmpty() && it != "null" }
                    .joinToString("\n") { "  • $it" }

                if (contenido.isEmpty()) "" else contenido
            }

            else -> {
                val texto = value.toString().trim()
                if (texto.isEmpty() || texto == "null") "" else texto
            }
        }
    }

    private fun limpiarDatosVacios(datos: Map<String, Any>): Map<String, Any> {
        val datosFiltrados = mutableMapOf<String, Any>()

        datos.forEach { (key, value) ->
            when (value) {
                is Map<*, *> -> {
                    val mapLimpio = value.filterValues {
                        it != null &&
                                it.toString().trim().isNotEmpty() &&
                                it.toString() != "null"
                    }
                    if (mapLimpio.isNotEmpty()) {
                        datosFiltrados[key] = mapLimpio
                    }
                }
                is List<*> -> {
                    val listaLimpia = value
                        .filterNotNull()
                        .filter {
                            it.toString().trim().isNotEmpty() &&
                                    it.toString() != "null"
                        }
                    if (listaLimpia.isNotEmpty()) {
                        datosFiltrados[key] = listaLimpia
                    }
                }
                else -> {
                    val texto = value.toString().trim()
                    if (texto.isNotEmpty() && texto != "null") {
                        datosFiltrados[key] = value
                    }
                }
            }
        }

        return datosFiltrados
    }
}