package com.uaa.registro_uaa

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegistroActivity : AppCompatActivity() {

    private lateinit var idRegistro: EditText
    private lateinit var R_Et_nombre_usuario: EditText
    private lateinit var R_Et_email: EditText
    private lateinit var R_Et_password: EditText
    private lateinit var R_Et_r_password: EditText
    private lateinit var Btn_registrar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)
        supportActionBar?.title = "Registros"
        inicializarVariables()

        Btn_registrar.setOnClickListener {
            validarDatos()
        }
    }

    private fun inicializarVariables() {
        idRegistro = findViewById(R.id.id_registro)
        R_Et_nombre_usuario = findViewById(R.id.R_Et_nombre_usuario)
        R_Et_email = findViewById(R.id.R_Et_email)
        R_Et_password = findViewById(R.id.R_Et_password)
        R_Et_r_password = findViewById(R.id.R_Et_r_password)
        Btn_registrar = findViewById(R.id.Btn_registrar)
    }

    private fun validarDatos() {
        val id = idRegistro.text.toString().trim()
        val nombre_usuario = R_Et_nombre_usuario.text.toString().trim()
        val email = R_Et_email.text.toString().trim()
        val password = R_Et_password.text.toString().trim()
        val r_password = R_Et_r_password.text.toString().trim()

        when {
            id.isEmpty() -> showToast("Ingrese su ID")

            // VALIDACIÓN DEL ID DE 6 DÍGITOS
            !id.matches(Regex("^\\d{6}$")) ->
                showToast("El ID debe tener exactamente 6 dígitos")

            nombre_usuario.isEmpty() -> showToast("Ingrese nombre de usuario")
            email.isEmpty() -> showToast("Ingrese su correo")
            !validarCorreoInstitucional(email) -> showToast("Correo inválido. Use formato institucional:\n- Alumno: alXXXXXX@edu.uaa.mx\n- Docente: nombre.apellido@edu.uaa.mx")
            password.isEmpty() -> showToast("Ingrese su contraseña")
            password.length < 8 -> showToast("Contraseña débil, debe tener al menos 8 caracteres")
            r_password.isEmpty() -> showToast("Por favor repita su contraseña")
            password != r_password -> showToast("Las contraseñas no coinciden")
            else -> registrarUsuario(id, email, password, nombre_usuario)
        }
    }


    /**
     * Valida el formato del correo institucional para alumno o docente.
     */
    private fun validarCorreoInstitucional(email: String): Boolean {
        // Expresiones regulares
        val regexAlumno = Regex("^al\\d{6}@edu\\.uaa\\.mx$")
        val regexDocente = Regex("^[a-zA-Z]+(\\.[a-zA-Z]+)+@edu\\.uaa\\.mx$")

        return regexAlumno.matches(email) || regexDocente.matches(email)
    }

    private fun registrarUsuario(id: String, email: String, password: String, nombreUsuario: String) {
        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid ?: ""
                    val datosUsuario = hashMapOf(
                        "id" to id,
                        "uid" to uid,
                        "nombre" to nombreUsuario,
                        "email" to email
                    )

                    db.collection("usuarios").document(uid).set(datosUsuario)
                        .addOnSuccessListener {
                            showToast("Usuario registrado y guardado en Firestore")
                        }
                        .addOnFailureListener { e ->
                            showToast("Error al guardar en Firestore: ${e.message}")
                        }
                } else {
                    showToast("Error al registrar usuario: ${task.exception?.message}")
                }
            }
    }

    private fun showToast(msg: String) {
        Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
    }
}
