package com.tuapp.salud // Cambia según tu paquete

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.tuapp.SaneamientoBasicoActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.uaa.registro_uaa.R

class DatosIdentificacionActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    // EditTexts

    private lateinit var etIdEstudiante: EditText

    private lateinit var nombreInstitucion: EditText
    private lateinit var jurisdiccionSanitaria: EditText
    private lateinit var municipio: EditText
    private lateinit var estado: EditText
    private lateinit var apellidoFamiliar: EditText
    private lateinit var responsableFamilia: EditText
    private lateinit var domicilio: EditText
    private lateinit var numExt: EditText
    private lateinit var numInt: EditText
    private lateinit var colonia: EditText
    private lateinit var noManzana: EditText
    private lateinit var conformacionFamiliar: EditText
    private lateinit var noIntegrantes: EditText
    private lateinit var lugarProcedencia: EditText
    private lateinit var religion: EditText
    private lateinit var seguridadSocial: EditText
    private lateinit var ingresoMensual: EditText
    private lateinit var noFamiliarEmigrados: EditText
    private lateinit var motivo: EditText
    private lateinit var expediente: EditText
    private lateinit var fechaElaboracion: EditText
    private lateinit var actualizacion: EditText
    private lateinit var rotacionEquipo: EditText
    private lateinit var btnGuardarSiguiente: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tarjeta_salud_familiar) // tu XML

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance()

        // Vincular EditTexts

        etIdEstudiante = findViewById(R.id.et_Id)

        nombreInstitucion = findViewById(R.id.nombreInstitucion)
        jurisdiccionSanitaria = findViewById(R.id.JurisdiccionSanitaria)
        municipio = findViewById(R.id.municipio)
        estado = findViewById(R.id.estado)
        apellidoFamiliar = findViewById(R.id.apellidoFamiliar)
        responsableFamilia = findViewById(R.id.responsableFamilia)
        domicilio = findViewById(R.id.domicilio)
        numExt = findViewById(R.id.numExt)
        numInt = findViewById(R.id.numInt)
        colonia = findViewById(R.id.colonia)
        noManzana = findViewById(R.id.no_manzana)
        conformacionFamiliar = findViewById(R.id.conformacionFamiliar)
        noIntegrantes = findViewById(R.id.no_integrantes)
        lugarProcedencia = findViewById(R.id.lugarProcedencia)
        religion = findViewById(R.id.religion)
        seguridadSocial = findViewById(R.id.seguridadSocial)
        ingresoMensual = findViewById(R.id.ingresoMensual)
        noFamiliarEmigrados = findViewById(R.id.no_familiarEmigrados)
        motivo = findViewById(R.id.motivo)
        expediente = findViewById(R.id.expediente)
        fechaElaboracion = findViewById(R.id.fechaElaboracion)
        actualizacion = findViewById(R.id.actualizacion)
        rotacionEquipo = findViewById(R.id.rotacion_Equipo)
        btnGuardarSiguiente = findViewById(R.id.btnGuardarSiguiente1)

        // Evento del botón Guardar
        btnGuardarSiguiente.setOnClickListener {

            // Crear ID de documento (puede ser apellido o cualquier identificador único)
           // val idDocumento = apellidoFamiliar.text.toString().trim()
           // if (idDocumento.isEmpty()) {
             //   Toast.makeText(this, "Ingresa el apellido para identificar el registro", Toast.LENGTH_SHORT).show()
               // return@setOnClickListener
            //}

            val datosIdentificacion= hashMapOf(
                "idEstudiante" to etIdEstudiante.text.toString(),
                "nombre_institucion" to nombreInstitucion.text.toString(),
                "jurisdiccion_sanitaria" to jurisdiccionSanitaria.text.toString(),
                "municipio" to municipio.text.toString(),
                "estado" to estado.text.toString(),
                "apellidos_familia" to apellidoFamiliar.text.toString(),
                "responsable_familia" to responsableFamilia.text.toString(),
                "domicilio" to domicilio.text.toString(),
                "num_exterior" to numExt.text.toString(),
                "num_interior" to numInt.text.toString(),
                "colonia" to colonia.text.toString(),
                "no_manzana" to noManzana.text.toString(),
                "conformacion_familiar" to conformacionFamiliar.text.toString(),
                "no_integrantes" to noIntegrantes.text.toString(),
                "lugar_procedencia" to lugarProcedencia.text.toString(),
                "religion" to religion.text.toString(),
                "seguridad_social" to seguridadSocial.text.toString(),
                "ingreso_mensual" to ingresoMensual.text.toString(),
                "familiares_emigrados" to noFamiliarEmigrados.text.toString(),
                "motivo" to motivo.text.toString(),
                "expediente" to expediente.text.toString(),
                "fecha_elaboracion" to fechaElaboracion.text.toString(),
                "actualizacion" to actualizacion.text.toString(),
                "rotacion_equipo" to rotacionEquipo.text.toString()

            )


            // Crear mapa de datos
            val datosFinales = hashMapOf(
                "Datos de Identificacion" to datosIdentificacion
            )






            // ✅ Generar ID automático
            val docRef = db.collection("Tarjeta_Salud").document()
            val idDocumento = docRef.id  // Guarda el ID generado

            docRef.set(datosFinales)
                .addOnSuccessListener {
                    Toast.makeText(this, "Datos guardados exitosamente", Toast.LENGTH_SHORT).show()

                    // se pasa el ID a la siguiente pantalla
                    val intent = Intent(this, SaneamientoBasicoActivity::class.java)
                    intent.putExtra("idDocumento", idDocumento)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al guardar: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }

    }
}

