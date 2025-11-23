package com.example.tuapp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.uaa.registro_uaa.R

class Alimentacion : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tarjeta_salud_familiar4)

        db = FirebaseFirestore.getInstance()

        // Obtener el idDocumento de la actividad anterior
        val idDocumento = intent.getStringExtra("idDocumento")
        if (idDocumento == null) {
            Toast.makeText(this, "No se recibió el ID del documento", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // === Referencias a los campos del primer CardView (Grupos de Alimentos) ===
        val etVerduras = findViewById<EditText>(R.id.etVerduras)
        val etCereales = findViewById<EditText>(R.id.etCereales)
        val etLeguminosas = findViewById<EditText>(R.id.etLeguminosas)

        // === Referencias a los campos del segundo CardView (Hábitos Alimenticios) ===
        val etGarrafones = findViewById<EditText>(R.id.etGarrafones)
        val etComidasDia = findViewById<EditText>(R.id.etComidasDia)
        val etTV = findViewById<EditText>(R.id.etTV)
        val etReunen = findViewById<EditText>(R.id.etReunen)
        val etColaciones = findViewById<EditText>(R.id.etColaciones)
        val etComidaFuerte = findViewById<EditText>(R.id.etComidaFuerte)

        // === Botón Guardar y Siguiente ===
        val btnGuardar = findViewById<Button>(R.id.btnGuardarSeccion4)

        btnGuardar.setOnClickListener {
            val datosAlimentacion = hashMapOf(
                "Verduras y frutas" to etVerduras.text.toString().trim(),
                "Cereales" to etCereales.text.toString().trim(),
                "Leguminosas y alimentos de origen animal" to etLeguminosas.text.toString().trim(),
                "Garrafones de agua consumidos a la semana" to etGarrafones.text.toString().trim(),
                "Número de comidas al día" to etComidasDia.text.toString().trim(),
                "Comen viendo televisión" to etTV.text.toString().trim(),
                "Se reúnen para comer" to etReunen.text.toString().trim(),
                "Consumen colaciones" to etColaciones.text.toString().trim(),
                "Comida más fuerte" to etComidaFuerte.text.toString().trim()
            )

            // Actualizar el documento existente en Firestore
            db.collection("Tarjeta_Salud")
                .document(idDocumento)
                .update("Alimentación", datosAlimentacion)
                .addOnSuccessListener {
                    Toast.makeText(this, "Datos actualizados exitosamente", Toast.LENGTH_SHORT).show()
                    // Ir a la siguiente sección
                    val intent = Intent(this, SeccionDemograficos::class.java)
                    intent.putExtra("idDocumento", idDocumento)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al actualizar: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}