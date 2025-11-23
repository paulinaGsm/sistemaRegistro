package com.uaa.registro_uaa

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore

class DetalleEntrevistaActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val documentId = intent.getStringExtra("documentId") ?: ""

        setContent {
            DetalleEntrevistaScreen(
                documentId = documentId,
                onBackClick = { finish() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleEntrevistaScreen(
    documentId: String,
    onBackClick: () -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    var isLoading by remember { mutableStateOf(true) }
    var entrevista by remember { mutableStateOf<Map<String, Any>>(emptyMap()) }

    LaunchedEffect(documentId) {
        db.collection("guia-valoracion").document(documentId)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    entrevista = doc.data ?: emptyMap()
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
                title = { Text("Detalle de Entrevista") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFC8E6C9),
                    titleContentColor = Color.DarkGray,
                    navigationIconContentColor = Color.DarkGray
                )
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF3C8D40))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFC8E6C9))
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Descriptivos de la persona
                item {
                    val descriptivos = entrevista["Descriptivos de la persona"] as? Map<*, *>
                    if (descriptivos != null) {
                        SeccionCard(
                            titulo = "Descriptivos de la Persona",
                            datos = descriptivos
                        )
                    }
                }

                // Características de entorno
                item {
                    val entorno = entrevista["Caracteristicas de entorno"] as? Map<*, *>
                    if (entorno != null) {
                        SeccionCard(
                            titulo = "Características de Entorno",
                            datos = entorno
                        )
                    }
                }

                // Patrón de vida
                item {
                    val patronVida = entrevista["Patron de vida"] as? Map<*, *>
                    if (patronVida != null) {
                        SeccionCard(
                            titulo = "Patrón de vida",
                            datos = patronVida
                        )
                    }
                }

                // Estado de salud
                item {
                    val Estadosalud = entrevista["Estado y sistema de salud"] as? Map<*, *>
                    if (Estadosalud != null) {
                        SeccionCard(
                            titulo = "Estado y sistema de salud",
                            datos = Estadosalud
                        )
                    }
                }

                //mantenimiento de un aporte de aire suficiente
                item {
                    val mantenimientoAire = entrevista["Mantenimiento de un aporte de aire suficiente"] as? Map<*, *>
                    if (mantenimientoAire != null) {
                        SeccionCard(
                            titulo = "Mantenimiento de un aporte de aire suficiente",
                            datos =  mantenimientoAire
                        )
                    }
                }

                //mantenimiento de un aporte de agua suficiente
                item {
                    val mantenimientoAgua = entrevista["Mantenimiento de un aporte de agua suficiente"] as? Map<*, *>
                    if (mantenimientoAgua != null) {
                        SeccionCard(
                            titulo = "Mantenimiento de un aporte de agua suficiente",
                            datos =  mantenimientoAgua
                        )
                    }
                }

                //mantenimiento de un aporte de alimento suficiente
                item {
                    val mantenimientoAlimento = entrevista["Mantenimiento de un aporte de alimento suficiente"] as? Map<*, *>
                    if (mantenimientoAlimento != null) {
                        SeccionCard(
                            titulo = "Mantenimiento de un aporte de alimento suficiente",
                            datos =  mantenimientoAlimento
                        )
                    }
                }


                //Prevencion de cuidados asociados con los procesos de eliminacion y los excrementos
                item {
                    val cuidadosExcremento = entrevista["Prevencion de cuidados asociados con los procesos de eliminacion y los excrementos"] as? Map<*, *>
                    if (cuidadosExcremento != null) {
                        SeccionCard(
                            titulo = "Prevención de cuidados asociados con los procesos de eliminación y los excrementos",
                            datos =  cuidadosExcremento
                        )
                    }
                }


                //Mantenimiento del equilibrio entre la actividad y el reposo
                item {
                    val mantenimientoEquilibrio = entrevista["Mantenimiento del equilibrio entre la actividad y el reposo"] as? Map<*, *>
                    if (mantenimientoEquilibrio != null) {
                        SeccionCard(
                            titulo = "Mantenimiento del equilibrio entre la actividad y el reposo",
                            datos =  mantenimientoEquilibrio
                        )
                    }
                }

                //Restriccion de actividad fisica
                item {
                    val restriccionFisica = entrevista["Restriccion de actividad fisica"] as? Map<*, *>
                    if (restriccionFisica != null) {
                        SeccionCard(
                            titulo = "Restricción de actividad fisica",
                            datos =  restriccionFisica
                        )
                    }
                }

                //Mantenimiento del equilibrio entre la sociedad y la interaccion humana
                item {
                    val mantenimientoInteraccionHumana= entrevista["Mantenimiento del equilibrio entre la sociedad y la interaccion humana"] as? Map<*, *>
                    if (mantenimientoInteraccionHumana != null) {
                        SeccionCard(
                            titulo = "Mantenimiento del equilibrio entre la sociedad y la interacción humana",
                            datos =  mantenimientoInteraccionHumana
                        )
                    }
                }


                //Prevencion de peligros para la vida, el funcionamiento y bienestar humano
                item {
                    val peligroVida= entrevista["Prevencion de peligros para la vida, el funcionamiento y bienestar humano"] as? Map<*, *>
                    if (peligroVida != null) {
                        SeccionCard(
                            titulo = "Prevención de peligros para la vida, el funcionamiento y bienestar humano ",
                            datos =  peligroVida
                        )
                    }
                }


                //Promocion del funcionamiento humano
                item {
                    val funcionamientoHumano= entrevista["Promocion del funcionamiento humano"] as? Map<*, *>
                    if (funcionamientoHumano != null) {
                        SeccionCard(
                            titulo = "Promoción del funcionamiento humano",
                            datos =  funcionamientoHumano
                        )
                    }
                }

                //Los que apoyan los procesos vitales
                item {
                    val procesosVitales= entrevista["Los que apoyan los procesos vitales"] as? Map<*, *>
                    if (procesosVitales != null) {
                        SeccionCard(
                            titulo = "Los que apoyan los procesos vitales",
                            datos =  procesosVitales
                        )
                    }
                }

                //Los que apoyan los procesos vitales (Mujeres)
                item {
                    val procesosVitalesMujer= entrevista["Los que apoyan los procesos vitales (Mujeres)"] as? Map<*, *>
                    if (procesosVitalesMujer != null) {
                        SeccionCard(
                            titulo = "Los que apoyan los procesos vitales (Mujeres)",
                            datos =  procesosVitalesMujer
                        )
                    }
                }

                //Los que apoyan los procesos vitales (Hombres)
                item {
                    val procesosVitalesHombre= entrevista["Los que apoyan los procesos vitales (Hombres)"] as? Map<*, *>
                    if (procesosVitalesHombre != null) {
                        SeccionCard(
                            titulo = "Los que apoyan los procesos vitales (Hombres)",
                            datos =  procesosVitalesHombre
                        )
                    }
                }

                //Desviaciones de la salud
                item {
                    val desviacionesSalud= entrevista["Desviaciones de la salud"] as? Map<*, *>
                    if (desviacionesSalud != null) {
                        SeccionCard(
                            titulo = "Desviaciones de la salud",
                            datos =  desviacionesSalud
                        )
                    }
                }






            }
        }
    }
}

@Composable
fun SeccionCard(
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
                color = Color(0xFF3C8D40),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Divider(color = Color.LightGray, thickness = 1.dp)

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