package com.uaa.registro_uaa


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore

class DetalleTarjetaSaludActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val documentId = intent.getStringExtra("documentId") ?: ""

        setContent {
            DetalleTarjetaSaludScreen(
                documentId = documentId,
                onBackClick = { finish() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleTarjetaSaludScreen(
    documentId: String,
    onBackClick: () -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    var isLoading by remember { mutableStateOf(true) }
    var tarjeta by remember { mutableStateOf<Map<String, Any>>(emptyMap()) }

    LaunchedEffect(documentId) {
        db.collection("Tarjeta_Salud").document(documentId)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    tarjeta = doc.data ?: emptyMap()
                }
                isLoading = false
            }
            .addOnFailureListener {
                isLoading = false
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Tarjeta de Salud") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2196F3),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF2196F3))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFE3F2FD))
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Datos de Identificación
                item {
                    val datosIdentificacion = tarjeta["Datos de Identificacion"] as? Map<*, *>
                    if (datosIdentificacion != null) {
                        SeccionCardSalud(
                            titulo = "Datos de Identificación",
                            datos = datosIdentificacion
                        )
                    }
                }

                // Alimentación
                item {
                    val alimentacion = tarjeta["Alimentación"] as? Map<*, *>
                    if (alimentacion != null) {
                        SeccionCardSalud(
                            titulo = "Alimentación",
                            datos = alimentacion
                        )
                    }
                }


                // datosCondicionantes
                item {
                    val datosCondicionates = tarjeta["Datos_Condicionantes"] as? Map<*, *>
                    if (datosCondicionates != null) {
                        SeccionCardSalud(
                            titulo = "Datos Condicionantes",
                            datos = datosCondicionates
                        )
                    }
                }

                // datosFamiliares
                item {
                    val datosFamiliares = tarjeta["Datos_familiares"] as? Map<*, *>
                    if (datosFamiliares != null) {
                        SeccionCardSalud(
                            titulo = "Datos Familiares",
                            datos = datosFamiliares
                        )
                    }
                }

                // saneamientoBasico
                item {
                    val saneamientoBasico = tarjeta["Saneamiento_Basico"] as? Map<*, *>
                    if (saneamientoBasico != null) {
                        SeccionCardSalud(
                            titulo = "Saneamiento Básico",
                            datos = saneamientoBasico
                        )
                    }
                }

                // cierreEncuesta
                item {
                    val cierreEncuesta = tarjeta["cierre_encuesta"] as? Map<*, *>
                    if (cierreEncuesta != null) {
                        SeccionCardSalud(
                            titulo = "Cierre Encuesta",
                            datos = cierreEncuesta
                        )
                    }
                }

                // estiloVida
                item {
                    val estiloVida = tarjeta["estilos_vida"] as? Map<*, *>
                    if (estiloVida != null) {
                        SeccionCardSalud(
                            titulo = "Estilo de Vida",
                            datos = estiloVida
                        )
                    }
                }


            }
        }
    }
}

@Composable
fun SeccionCardSalud(
    titulo: String,
    datos: Map<*, *>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = titulo,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2196F3),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            HorizontalDivider(color = Color.LightGray, thickness = 1.dp)

            Spacer(modifier = Modifier.height(12.dp))

            datos.forEach { (key, value) ->
                if (value != null && value.toString().isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = key.toString(),
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = value.toString(),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}