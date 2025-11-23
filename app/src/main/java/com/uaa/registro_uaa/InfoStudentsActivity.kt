package com.uaa.registro_uaa

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.graphics.Color
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class InfoStudentsActivity : ComponentActivity() {
    private val db = FirebaseFirestore.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Recibir datos del intent
        val name = intent.getStringExtra("name") ?: ""
        val email = intent.getStringExtra("email") ?: ""
        // val career = intent.getStringExtra("career") ?: ""
        //val semester = intent.getStringExtra("semester") ?: ""
        val colorInt = intent.getIntExtra("color", 0)
        val uid = intent.getStringExtra("uid") ?: ""


        val latitudes = intent.getDoubleArrayExtra("latitudes") ?: doubleArrayOf()
        val longitudes = intent.getDoubleArrayExtra("longitudes") ?: doubleArrayOf()

        val ubicaciones = latitudes.zip(longitudes) { lat, lon -> Pair(lat, lon) }


        val student = Student(
            name = name,
            email = email,
            uid = uid,
            //   career = career,
            //  semester = semester,
            backgroundColor = Color(colorInt),
            ubicaciones = ubicaciones
        )


        // Variables para usuario actual
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userUid = currentUser?.uid ?: ""
        var userName = "Usuario"
        var userEmail = currentUser?.email ?: "Sin correo"

        // Cargar nombre real desde Firestore
        if (userUid.isNotEmpty()) {
            db.collection("usuarios").document(userUid)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        userName = document.getString("nombre") ?: "Usuario"
                        userEmail = document.getString("email") ?: userEmail

                        // Ya con los datos actualizados, renderizamos
                        setContent {
                            StudentDetailScreen(
                                student = student,
                                userName = userName,
                                userEmail = userEmail,
                                onBackClick = { finish() },
                                onRegistroClick = { documentId ->
                                    val intent =
                                        Intent(this, DetalleEntrevistaActivity::class.java).apply {
                                            putExtra("documentId", documentId)
                                        }
                                    startActivity(intent)
                                }
                            )
                        }
                    }
                }
        }


    }
}
