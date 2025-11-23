package com.uaa.registro_uaa

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.firebase.auth.FirebaseAuth

class Inicio : AppCompatActivity() {


    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle


    private lateinit var Btn_ir_registros : Button
    private lateinit var Btn_ir_logueo : Button

    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio)



        Btn_ir_registros = findViewById(R.id.Btn_ir_registros)
        Btn_ir_logueo = findViewById(R.id.Btn_ir_logueo)

        Btn_ir_registros.setOnClickListener {
            val intent = Intent(this@Inicio, RegistroActivity::class.java)
            Toast.makeText(applicationContext, "Registros", Toast.LENGTH_SHORT).show()
            startActivity(intent)
        }
        Btn_ir_logueo.setOnClickListener {
            val intent = Intent(this@Inicio,LoginActivity::class.java)
            Toast.makeText(applicationContext, "Login", Toast.LENGTH_SHORT).show()
            startActivity(intent)
        }
        }
    }
