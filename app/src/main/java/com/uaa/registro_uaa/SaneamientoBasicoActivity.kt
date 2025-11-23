package com.example.tuapp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.uaa.registro_uaa.R

class SaneamientoBasicoActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    // Declaración de campos
    private lateinit var etDormitorios: EditText
    private lateinit var etHabitaciones: EditText
    private lateinit var etVentanas: EditText
    private lateinit var etCocina: EditText
    private lateinit var etCombustible: EditText
    private lateinit var etLuzElectrica: EditText
    private lateinit var etPiso: EditText
    private lateinit var etParedes: EditText
    private lateinit var etTechos: EditText
    private lateinit var etFuenteAgua: EditText
    private lateinit var etLlegaAgua: EditText
    private lateinit var etAguaConsumo: EditText
    private lateinit var etLavaManosComer: EditText
    private lateinit var etLavaManosWC: EditText
    private lateinit var etHigieneBucal: EditText
    private lateinit var etBañoCompleto: EditText
    private lateinit var etDesechoExcretas: EditText
    private lateinit var etTratamientoBasura: EditText
    private lateinit var etFaunaNociva: EditText
    private lateinit var etAnimalesDomesticos: EditText
    private lateinit var etAnimalesVacunados: EditText
    private lateinit var etExpendeProductos: EditText
    private lateinit var etPermisosReglamentario: EditText

    private lateinit var btnGuardarSiguiente: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tarjeta_salud_familiar2)

        // Inicializar campos
        etDormitorios = findViewById(R.id.et_dormitorios)
        etHabitaciones = findViewById(R.id.et_habitaciones)
        etVentanas = findViewById(R.id.et_ventanas)
        etCocina = findViewById(R.id.et_cocina)
        etCombustible = findViewById(R.id.et_combustible)
        etLuzElectrica = findViewById(R.id.et_luzelectrica)
        etPiso = findViewById(R.id.et_piso)
        etParedes = findViewById(R.id.et_paredes)
        etTechos = findViewById(R.id.et_techos)
        etFuenteAgua = findViewById(R.id.et_fuenteAgua)
        etLlegaAgua = findViewById(R.id.et_llegaAgua)
        etAguaConsumo = findViewById(R.id.et_aguaConsumo)
        etLavaManosComer = findViewById(R.id.et_lavaManosComer)
        etLavaManosWC = findViewById(R.id.et_lavaManosWC)
        etHigieneBucal = findViewById(R.id.et_higieneBucal)
        etBañoCompleto = findViewById(R.id.et_bañoCompleto)
        etDesechoExcretas = findViewById(R.id.et_desechoExcretas)
        etTratamientoBasura = findViewById(R.id.et_tratamientoBasura)
        etFaunaNociva = findViewById(R.id.et_faunaNociva)
        etAnimalesDomesticos = findViewById(R.id.et_animalesDomesticos)
        etAnimalesVacunados = findViewById(R.id.et_animalesVacunados)
        etExpendeProductos = findViewById(R.id.et_expendeProductos)
        etPermisosReglamentario = findViewById(R.id.et_permisosReglamentario)
        btnGuardarSiguiente = findViewById(R.id.btnGuardarSiguiente2)

        // Obtener el idDocumento de la actividad anterior
        val idDocumento = intent.getStringExtra("idDocumento")
        if (idDocumento == null) {
            Toast.makeText(this, "No se recibió el ID del documento", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Acción del botón
        btnGuardarSiguiente.setOnClickListener {
            guardarDatos(idDocumento)
        }
    }

    private fun guardarDatos(idDocumento: String) {
        val saneamientoBasico = hashMapOf(
            "NumeroDormitorios" to etDormitorios.text.toString(),
            "TotalHabitaciones" to etHabitaciones.text.toString(),
            "NumeroVentanas" to etVentanas.text.toString(),
            "CocinaSeparada" to etCocina.text.toString(),
            "TipoCombustible" to etCombustible.text.toString(),
            "LuzElectrica" to etLuzElectrica.text.toString(),
            "Piso" to etPiso.text.toString(),
            "Paredes" to etParedes.text.toString(),
            "Techos" to etTechos.text.toString(),
            "FuenteAgua" to etFuenteAgua.text.toString(),
            "LlegaAgua" to etLlegaAgua.text.toString(),
            "AguaConsumoHumano" to etAguaConsumo.text.toString(),
            "LavaManosAntesComer" to etLavaManosComer.text.toString(),
            "LavaManosPosteriorWC" to etLavaManosWC.text.toString(),
            "HigieneBucal" to etHigieneBucal.text.toString(),
            "BañoCompleto" to etBañoCompleto.text.toString(),
            "DesechoExcretas" to etDesechoExcretas.text.toString(),
            "TratamientoBasura" to etTratamientoBasura.text.toString(),
            "FaunaNociva" to etFaunaNociva.text.toString(),
            "AnimalesDomesticos" to etAnimalesDomesticos.text.toString(),
            "AnimalesVacunados" to etAnimalesVacunados.text.toString(),
            "ExpendeProductos" to etExpendeProductos.text.toString(),
            "PermisosReglamentarios" to etPermisosReglamentario.text.toString()
        )


/*
        val datosFinales = hashMapOf(
            "Saneamiento Basico" to saneamientoBasico
        )

  */


        // Actualizar el documento existente
        db.collection("Tarjeta_Salud")
            .document(idDocumento)
            .update("Saneamiento_Basico", saneamientoBasico)
            .addOnSuccessListener {
                Toast.makeText(this, "Datos actualizados exitosamente", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, DatosAdicionalesActivity::class.java)
                intent.putExtra("idDocumento", idDocumento) // seguir pasando el mismo ID
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al actualizar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
