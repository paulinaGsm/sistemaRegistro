package com.uaa.registro_uaa

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        MostrarBienvenida()
    }

        fun MostrarBienvenida(){
            object : CountDownTimer( 800, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    //TODO("Not yet implemented")
                }

                override fun onFinish() {
                    val intent = Intent(applicationContext,Inicio::class.java)
                    startActivity(intent)
                    finish()
                }
            }.start()
        }
}
