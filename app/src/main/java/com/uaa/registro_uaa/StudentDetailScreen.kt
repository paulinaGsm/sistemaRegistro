package com.uaa.registro_uaa

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

// DATA CLASS
data class RegistroEntrevista(
    val idEstudiante: String,
    val nombreEntrevistado: String,
    val fecha: String,
    val documentId: String,
    val tipo: String // "valoracion" o "tarjeta_salud"
)

// FUNCIÓN PRINCIPAL
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDetailScreen(
    student: Student,
    userName: String,
    userEmail: String,
    onBackClick: () -> Unit,
    onRegistroClick: (String) -> Unit
) {
    var ubicaciones by remember { mutableStateOf(listOf<Pair<Double, Double>>()) }
    var registros by remember { mutableStateOf<List<RegistroEntrevista>>(emptyList()) }
    var registrosFiltrados by remember { mutableStateOf<List<RegistroEntrevista>>(emptyList()) }
    var isLoadingRegistros by remember { mutableStateOf(true) }

    // Estados para el filtro
    var fechaInicio by remember { mutableStateOf<String?>(null) }
    var fechaFin by remember { mutableStateOf<String?>(null) }
    var mostrarFiltros by remember { mutableStateOf(false) }

    // ✅ Estado para mostrar todas las ubicaciones
    var mostrarTodasUbicaciones by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    val calendario = Calendar.getInstance()

    // Cargar ubicaciones
    LaunchedEffect(student.uid) {
        db.collection("usuarios").document(student.uid)
            .collection("locations")
            .get()
            .addOnSuccessListener { result ->
                ubicaciones = result.mapNotNull {
                    val lat = it.getDouble("Latitud")
                    val lon = it.getDouble("Longitud")
                    if (lat != null && lon != null) Pair(lat, lon) else null
                }
            }
    }

    // Cargar registros
    LaunchedEffect(student.uid) {
        db.collection("usuarios").document(student.uid)
            .get()
            .addOnSuccessListener { userDoc ->
                val idDelEstudiante = userDoc.getString("id")
                val registrosTemp = mutableListOf<RegistroEntrevista>()

                if (idDelEstudiante != null) {
                    db.collection("guia-valoracion")
                        .whereEqualTo("Descriptivos de la persona.idEstudiante", idDelEstudiante)
                        .get()
                        .addOnSuccessListener { docs1 ->
                            registrosTemp.addAll(
                                docs1.mapNotNull { doc ->
                                    try {
                                        RegistroEntrevista(
                                            idEstudiante = doc.get("Descriptivos de la persona.idEstudiante")?.toString() ?: "",
                                            nombreEntrevistado = doc.get("Descriptivos de la persona.Nombre")?.toString() ?: "",
                                            fecha = doc.getString("Descriptivos de la persona.Fecha de Ingreso") ?: "Sin fecha",
                                            documentId = doc.id,
                                            tipo = "valoracion"
                                        )
                                    } catch (e: Exception) {
                                        null
                                    }
                                }
                            )

                            db.collection("Tarjeta_Salud")
                                .whereEqualTo("Datos de Identificacion.idEstudiante", idDelEstudiante)
                                .get()
                                .addOnSuccessListener { docs2 ->
                                    registrosTemp.addAll(
                                        docs2.mapNotNull { doc ->
                                            try {
                                                RegistroEntrevista(
                                                    idEstudiante = doc.get("Datos de Identificacion.idEstudiante")?.toString() ?: "",
                                                    nombreEntrevistado = doc.get("Datos de Identificacion.apellidos_familia")?.toString() ?: "",
                                                    fecha = doc.getString("Datos de Identificacion.actualizacion") ?: "Sin fecha",
                                                    documentId = doc.id,
                                                    tipo = "tarjeta_salud"
                                                )
                                            } catch (e: Exception) {
                                                null
                                            }
                                        }
                                    )

                                    registros = registrosTemp.sortedByDescending { it.fecha }
                                    registrosFiltrados = registros
                                    isLoadingRegistros = false
                                }
                                .addOnFailureListener {
                                    registros = registrosTemp
                                    registrosFiltrados = registrosTemp
                                    isLoadingRegistros = false
                                    Toast.makeText(context, "Error al cargar tarjetas de salud", Toast.LENGTH_SHORT).show()
                                }
                        }
                        .addOnFailureListener {
                            isLoadingRegistros = false
                            Toast.makeText(context, "Error al cargar registros", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    isLoadingRegistros = false
                }
            }
            .addOnFailureListener {
                isLoadingRegistros = false
                Toast.makeText(context, "Error al obtener información del usuario", Toast.LENGTH_SHORT).show()
            }
    }

    // Función para filtrar registros
    fun aplicarFiltro() {
        registrosFiltrados = if (fechaInicio == null && fechaFin == null) {
            registros
        } else {
            registros.filter { registro ->
                val fechaRegistro = try {
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(registro.fecha)
                } catch (e: Exception) {
                    null
                }

                if (fechaRegistro == null) return@filter false

                val cumpleFechaInicio = fechaInicio?.let {
                    val inicio = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(it)
                    fechaRegistro >= inicio
                } ?: true

                val cumpleFechaFin = fechaFin?.let {
                    val fin = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(it)
                    fechaRegistro <= fin
                } ?: true

                cumpleFechaInicio && cumpleFechaFin
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFC8E6C9))
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        // Header con información del estudiante
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(student.backgroundColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                student.name.take(2).uppercase(),
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = student.name,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF506A32)
                            )
                            Text(
                                text = student.email,
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }

        // Sección de ubicaciones (SIN MAPA - Solución estable)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Lugares visitados",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF506A32)
                        )
                        Text(
                            text = "${ubicaciones.size} ubicaciones",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (ubicaciones.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "Sin ubicaciones",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Sin ubicaciones registradas", color = Color.Gray)
                            }
                        }
                    } else {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // ✅ Mostrar solo 4 ubicaciones o todas según el estado
                            val ubicacionesMostrar = if (mostrarTodasUbicaciones) {
                                ubicaciones
                            } else {
                                ubicaciones.take(4)
                            }

                            ubicacionesMostrar.forEachIndexed { index, (lat, lon) ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            val uri = Uri.parse("geo:$lat,$lon?q=$lat,$lon")
                                            val intent = Intent(Intent.ACTION_VIEW, uri)
                                            context.startActivity(intent)
                                        },
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(0xFFF1F8E9)
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .background(
                                                    Color(0xFF2E7D32),
                                                    shape = CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "${index + 1}",
                                                color = Color.White,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                "Lat: %.6f".format(lat),
                                                fontSize = 12.sp,
                                                color = Color.Gray
                                            )
                                            Text(
                                                "Lon: %.6f".format(lon),
                                                fontSize = 12.sp,
                                                color = Color.Gray
                                            )
                                        }
                                        Icon(
                                            imageVector = Icons.Default.ArrowForward,
                                            contentDescription = "Ver en Maps",
                                            tint = Color(0xFF2E7D32)
                                        )
                                    }
                                }
                            }

                            // ✅ Botón "Ver más" / "Ver menos"
                            if (ubicaciones.size > 4) {
                                OutlinedButton(
                                    onClick = { mostrarTodasUbicaciones = !mostrarTodasUbicaciones },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = Color(0xFF2E7D32)
                                    )
                                ) {
                                    Icon(
                                        imageVector = if (mostrarTodasUbicaciones)
                                            Icons.Default.KeyboardArrowUp
                                        else
                                            Icons.Default.KeyboardArrowDown,
                                        contentDescription = null
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        if (mostrarTodasUbicaciones)
                                            "Ver menos"
                                        else
                                            "Ver todas (${ubicaciones.size} ubicaciones)"
                                    )
                                }
                            }

                            // Botón para abrir en Google Maps
                            Button(
                                onClick = {
                                    launchMapaActivity(context, student.uid)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF2E7D32)
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Place,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Abrir mapa completo") // Texto actualizado
                            }
                        }
                    }
                }
            }
        }

        // Header de registros CON FILTROS
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Registros de Entrevistas",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF506A32)
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            IconButton(
                                onClick = { mostrarFiltros = !mostrarFiltros }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = "Filtrar por fecha",
                                    tint = Color(0xFF00BCD4)
                                )
                            }

                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFF00BCD4)
                                ),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Text(
                                    text = "${registrosFiltrados.size}",
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }

                    if (mostrarFiltros) {
                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Filtrar por rango de fechas",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF506A32)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Desde:", fontSize = 12.sp, color = Color.Gray)
                            OutlinedButton(
                                onClick = {
                                    DatePickerDialog(
                                        context,
                                        { _, year, month, day ->
                                            fechaInicio = String.format("%02d/%02d/%04d", day, month + 1, year)
                                            aplicarFiltro()
                                        },
                                        calendario.get(Calendar.YEAR),
                                        calendario.get(Calendar.MONTH),
                                        calendario.get(Calendar.DAY_OF_MONTH)
                                    ).show()
                                }
                            ) {
                                Text(
                                    text = fechaInicio ?: "Seleccionar",
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Hasta:", fontSize = 12.sp, color = Color.Gray)
                            OutlinedButton(
                                onClick = {
                                    DatePickerDialog(
                                        context,
                                        { _, year, month, day ->
                                            fechaFin = String.format("%02d/%02d/%04d", day, month + 1, year)
                                            aplicarFiltro()
                                        },
                                        calendario.get(Calendar.YEAR),
                                        calendario.get(Calendar.MONTH),
                                        calendario.get(Calendar.DAY_OF_MONTH)
                                    ).show()
                                }
                            ) {
                                Text(
                                    text = fechaFin ?: "Seleccionar",
                                    fontSize = 12.sp
                                )
                            }
                        }

                        if (fechaInicio != null || fechaFin != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            TextButton(
                                onClick = {
                                    fechaInicio = null
                                    fechaFin = null
                                    aplicarFiltro()
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Limpiar",
                                    tint = Color(0xFFFF5722)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Limpiar filtros", color = Color(0xFFFF5722))
                            }
                        }
                    }
                }
            }
        }

        if (isLoadingRegistros) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF00BCD4))
                }
            }
        }

        if (!isLoadingRegistros && registrosFiltrados.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📋", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                if (fechaInicio != null || fechaFin != null)
                                    "No hay entrevistas en este rango de fechas"
                                else
                                    "No ha realizado entrevistas",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }

        items(registrosFiltrados) { registro ->
            RegistroCard(
                registro = registro,
                onClick = {
                    val intent = if (registro.tipo == "valoracion") {
                        Intent(context, DetalleEntrevistaActivity::class.java)
                    } else {
                        Intent(context, DetalleTarjetaSaludActivity::class.java)
                    }
                    intent.putExtra("documentId", registro.documentId)
                    context.startActivity(intent)
                }
            )
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

fun launchMapaActivity(context: Context, studentUid: String) {
    val intent = Intent(context, MapaActivityDocente::class.java)
    intent.putExtra("studentUid", studentUid) // ✅ IMPORTANTE: Pasar el UID
    context.startActivity(intent)
}
@Composable
fun RegistroCard(
    registro: RegistroEntrevista,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (registro.tipo == "valoracion") Color(0xFFF5F5F5) else Color(0xFFE3F2FD)
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (registro.tipo == "valoracion") Color(0xFF6B9963) else Color(0xFF2196F3)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "Icono Persona",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Surface(
                    color = if (registro.tipo == "valoracion") Color(0xFF4CAF50) else Color(0xFF2196F3),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = if (registro.tipo == "valoracion") "Guía de Valoración" else "Tarjeta de Salud",
                        fontSize = 10.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Nombre: ${registro.nombreEntrevistado}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
                Text(
                    text = "ID: ${registro.idEstudiante}",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
                Text(
                    text = "Fecha: ${registro.fecha}",
                    fontSize = 12.sp,
                    color = Color(0xFF506A32)
                )
            }
        }
    }
}

@Composable
fun InfoItem(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF506A32),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
        }
        Text(
            text = value,
            fontSize = 14.sp,
            color = Color.Black,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(start = 24.dp, top = 2.dp)
        )
    }
}