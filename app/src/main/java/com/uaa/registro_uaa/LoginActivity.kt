package com.uaa.registro_uaa

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var L_Et_email: EditText
    private lateinit var L_Et_password: EditText
    private lateinit var Btn_login: Button
    private lateinit var Rg_tipo_usuario: RadioGroup
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        inicializarVariables()

        Btn_login.setOnClickListener {
            loginUsuario()
        }
    }

    private fun inicializarVariables() {
        L_Et_email = findViewById(R.id.L_Et_email)
        L_Et_password = findViewById(R.id.L_Et_password)
        Btn_login = findViewById(R.id.Btn_login)
        Rg_tipo_usuario = findViewById(R.id.Rg_tipo_usuario)
    }

    private fun loginUsuario() {
        val email = L_Et_email.text.toString().trim()
        val password = L_Et_password.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            showToast("Por favor, llena todos los campos")
            return
        }

        // Obtener el tipo de usuario seleccionado
        val selectedRol = if (Rg_tipo_usuario.checkedRadioButtonId == R.id.Rb_docente) {
            "docente"
        } else {
            "alumno"
        }

        //  Validar formato de correo según el tipo de usuario
        if (!validarCorreoPorRol(email, selectedRol)) {
            if (selectedRol == "alumno") {
                showToast("Correo inválido. Usa formato al######@edu.uaa.mx (6 dígitos)")  // Mensaje actualizado
            } else {
                showToast("Correo inválido. Usa formato nombre.apellido@edu.uaa.mx")
            }
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    //  Validación: si el correo empieza con "al" NO puede ser docente
                    if (selectedRol == "docente" && email.startsWith("al", ignoreCase = true)) {
                        showToast("No tienes permisos de docente, usa el rol Alumno")
                        return@addOnCompleteListener
                    }

                    showToast("Bienvenido")

                    // Redirigir según el rol
                    if (selectedRol == "docente") {
                        startActivity(Intent(this, ListStudentsActivity::class.java))
                    } else {
                        startActivity(Intent(this, MenuPrincipalActivity::class.java))
                    }

                    finish()
                } else {
                    showToast("Usuario no encontrado o contraseña incorrecta")
                }
            }

        // Extraer ID numérico si es alumno
        if (selectedRol == "alumno" && email.startsWith("al", true)) {
            val idNumerico = email.removePrefix("al").substringBefore("@")
            val prefs = getSharedPreferences("usuario_prefs", Context.MODE_PRIVATE)
            prefs.edit().putString("idEstudiante", idNumerico).apply()
        }
    }

    /**
     * Valida el formato del correo según el rol.
     */
    private fun validarCorreoPorRol(email: String, rol: String): Boolean {
        // Expresiones regulares
        val regexAlumno = Regex("^al\\d{6}@edu\\.uaa\\.mx$")
        val regexDocente = Regex("^[a-zA-Z]+(\\.[a-zA-Z]+)?@edu\\.uaa\\.mx$", RegexOption.IGNORE_CASE)

        return when (rol) {
            "alumno" -> regexAlumno.matches(email)
            "docente" -> regexDocente.matches(email)
            else -> false
        }
    }

    private fun showToast(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }
}
