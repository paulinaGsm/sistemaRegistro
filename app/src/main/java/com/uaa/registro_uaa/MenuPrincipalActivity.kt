package com.uaa.registro_uaa

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.tuapp.salud.DatosIdentificacionActivity

class MenuPrincipalActivity : AppCompatActivity() {

    private lateinit var btnSaludFamiliar: Button
    private lateinit var btnCodigoTarjeta: Button
    private lateinit var btnGuiaValoracion: Button
    private lateinit var btnMapa: Button
    private lateinit var btnMapaManzana: Button
    private lateinit var btnpdfTarjeta: Button //tarjeta de salud
    private lateinit var btnCerrarSesion: ImageButton // Icono en header
    // private lateinit var btnCerrarSesionAlt: Button // Botón alternativo (si lo usas)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_principal)
        supportActionBar?.title = "Menú Principal"

        // Configuración de los botones del menú
        btnSaludFamiliar = findViewById(R.id.btn_salud_familiar)
        btnCodigoTarjeta = findViewById(R.id.btn_codigo_tarjeta)
        btnGuiaValoracion = findViewById(R.id.btn_guia_valoracion)
        btnMapa = findViewById(R.id.btn_mapa)
        btnpdfTarjeta= findViewById(R.id.btn_pdfTarjeta)
        btnMapaManzana = findViewById(R.id.btn_mapaManzana)

        // Configurar botón de cerrar sesión (icono en header)
        btnCerrarSesion = findViewById(R.id.btn_cerrar_sesion)
        btnCerrarSesion.setOnClickListener {
            mostrarDialogoCerrarSesion()
        }

        // Si usas el botón alternativo al final del layout, descomenta esto:
        // btnCerrarSesionAlt = findViewById(R.id.btn_cerrar_sesion_alt)
        // btnCerrarSesionAlt.setOnClickListener {
        //     mostrarDialogoCerrarSesion()
        // }

        // Listeners de los botones del menú
        btnSaludFamiliar.setOnClickListener {
            showToast("Salud Familiar seleccionado")
            startActivity(Intent(this, DatosIdentificacionActivity::class.java))
        }

        btnCodigoTarjeta.setOnClickListener {
            val intent = Intent(this, DescargarReporteActivity::class.java)
            startActivity(intent)
        }

        btnpdfTarjeta.setOnClickListener {
            val intent = Intent(this, DescargarReporteTActivity::class.java)
            startActivity(intent)
        }

        btnGuiaValoracion.setOnClickListener {
            val intent = Intent(this, GuiaValoracionActivity::class.java)
            startActivity(intent)
        }

        btnMapa.setOnClickListener {
            val intent = Intent(this, MapaActivity::class.java)
            startActivity(intent)
        }

        btnMapaManzana.setOnClickListener {
            val intent = Intent(this, verManzanasActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * Muestra un diálogo de confirmación antes de cerrar sesión
     */
    private fun mostrarDialogoCerrarSesion() {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Cerrar Sesión")
            .setMessage("¿Estás seguro de que deseas cerrar tu sesión?")
            .setPositiveButton("Sí, cerrar", null)
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.setOnShowListener {
            // Personalizar botón positivo (Sí, cerrar)
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).apply {
                setTextColor(resources.getColor(android.R.color.holo_red_dark))
                setOnClickListener {
                    cerrarSesion()
                    dialog.dismiss()
                }
            }

            // Personalizar botón negativo (Cancelar)
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).apply {
                setTextColor(resources.getColor(android.R.color.darker_gray))
                setOnClickListener {
                    dialog.dismiss()
                }
            }
        }

        dialog.show()
    }

    /**
     * Cierra la sesión del usuario
     */
    private fun cerrarSesion() {
        // Limpiar SharedPreferences
        val prefs = getSharedPreferences("usuario_prefs", Context.MODE_PRIVATE)
        prefs.edit().clear().apply()

        // Opcional: Cerrar sesión de Firebase si lo usas
        FirebaseAuth.getInstance().signOut()

        // Mostrar mensaje de confirmación
        Toast.makeText(this, "Sesión cerrada correctamente", Toast.LENGTH_SHORT).show()

        // Redirigir al Login
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun showToast(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }
}