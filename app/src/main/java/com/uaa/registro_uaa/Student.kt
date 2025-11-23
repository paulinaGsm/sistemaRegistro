package com.uaa.registro_uaa

import androidx.compose.ui.graphics.Color

data class Student(
    val name: String = "",
    val email: String = "",
    val uid: String = "",
    val backgroundColor: Color,
    val ubicaciones: List<Pair<Double, Double>> = emptyList()
    //val backgroundColor: Color = Color(0xFF4DD0E1) // puedes asignar color por default
)
