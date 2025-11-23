package com.uaa.registro_uaa

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tuapp.salud.DatosIdentificacionActivity

class CodigoTarjetaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_codigo_tarjeta)
        supportActionBar?.title = "Código de Tarjeta"

        findViewById<Button>(R.id.btn_tarjetaSalud).setOnClickListener {
            showToast("ttt")
            startActivity(Intent(this, DatosIdentificacionActivity::class.java))
        }

        findViewById<Button>(R.id.btn_codigoValoracion).setOnClickListener {
            showToast("Ssss")
            startActivity(Intent(this, DatosIdentificacionActivity::class.java))
        }


    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
