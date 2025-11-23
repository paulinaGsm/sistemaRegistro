package com.uaa.registro_uaa

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.random.Random


import android.graphics.Color as AndroidColor
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.QuerySnapshot
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

private var students = mutableStateListOf<Student>()

class ListStudentsActivity : ComponentActivity() {

    private val db = FirebaseFirestore.getInstance()



    private var currentUserName by mutableStateOf("")
    private var currentUserEmail by mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        cargarUsuario()
        cargarEstudiantes()

        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUserEmail = currentUser?.email ?: "Sin correo"

        setContent {
            StudentListScreen(
                students = students,
                currentUserName = currentUserName,
                currentUserEmail = currentUserEmail
            ) { student ->
                val intent = Intent(this, InfoStudentsActivity::class.java).apply {
                    putExtra("name", student.name)
                    putExtra("email", student.email)
                    putExtra("uid", student.uid)
                    putExtra("color", student.backgroundColor.value.toInt())

                    val latitudes = student.ubicaciones.map { it.first }.toDoubleArray()
                    val longitudes = student.ubicaciones.map { it.second }.toDoubleArray()
                    putExtra("latitudes", latitudes)
                    putExtra("longitudes", longitudes)
                }
                startActivity(intent)
            }
        }
    }

    private fun cargarUsuario() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        db.collection("usuarios").document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    currentUserName = document.getString("nombre") ?: "Usuario"
                }
            }
    }

    private fun cargarEstudiantes() {
        db.collection("usuarios")
            .get()
            .addOnSuccessListener { result ->
                students.clear()

                for (document in result) {
                    val name = document.getString("nombre") ?: ""
                    val email = document.getString("email") ?: ""
                    val uid = document.getString("uid") ?: ""

                    // Filtrar solo alumnos
                    if (!email.startsWith("al", ignoreCase = true)) continue

                    val color = Color(0xFF000000 or ((0xFFFFFF and Random.nextInt()).toLong()))

                    val baseStudent = Student(name, email, uid, color)

                    // Leer subcolección de ubicaciones
                    db.collection("usuarios")
                        .document(uid)
                        .collection("locations")
                        .get()
                        .addOnSuccessListener { locSnapshot ->
                            val ubicaciones = locSnapshot.documents.mapNotNull { locDoc ->
                                val lat = locDoc.getDouble("Latitud")
                                val lon = locDoc.getDouble("Longitud")
                                if (lat != null && lon != null) Pair(lat, lon) else null
                            }

                            students.add(baseStudent.copy(ubicaciones = ubicaciones))
                        }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar estudiantes: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }



}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentListScreen(
    students: List<Student>,
    currentUserName: String,
    currentUserEmail: String,
    onStudentClick: (Student) -> Unit
) {
    var searchText by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFF204722))) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF204722)),
            shape = RoundedCornerShape(12.dp)
        )
        {

            // ✅ Ícono del lado izquierdo
          //  Icon(
            //    imageVector = Icons.Default.Person,
             //   contentDescription = "Icono de usuario",
               // tint = Color.White,
                //modifier = Modifier.size(48.dp)
            //)

            Spacer(modifier = Modifier.width(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon( imageVector = Icons.Default.Person,
                    contentDescription = "Icono de usuario",
                    tint = Color.White,
                    modifier = Modifier.size(48.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(currentUserName, color = Color.White, fontSize = 18.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                    Text(currentUserEmail, color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    placeholder = { Text("Buscar estudiante...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(25.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(students.filter {
                        it.name.contains(searchText, ignoreCase = true) || it.email.contains(searchText, ignoreCase = true)
                    }) { student ->
                        StudentCard(student = student, onClick = { onStudentClick(student) })
                    }
                }
            }
        }
    }
}

@Composable
fun StudentCard(student: Student, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(50.dp).clip(CircleShape).background(student.backgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Text(student.name.take(2).uppercase(), color = Color.White, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(student.name, fontSize = 16.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold)
                Text(student.email, fontSize = 14.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Medium, color = Color(0xFF00BCD4))
            }
        }
    }
}

@Composable
fun StudentDetailScreenWithOSM(
    student: Student,
    userName: String,
    userEmail: String,
    onBackClick: () -> Unit
) {
    var ubicaciones by remember { mutableStateOf(listOf<Pair<Double, Double>>()) }
    val context = LocalContext.current

    // Cargar ubicaciones desde Firebase
    LaunchedEffect(student.uid) {
        val db = FirebaseFirestore.getInstance()
        db.collection("usuarios").document(student.uid)
            .collection("ubicaciones")
            .get()
            .addOnSuccessListener { result ->
                ubicaciones = result.map {
                    Pair(it.getDouble("latitude") ?: 0.0, it.getDouble("longitude") ?: 0.0)
                }
            }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Información del estudiante
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFC8E6C9)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = student.name, fontSize = 20.sp, color = Color.White)
                Text(text = student.email, fontSize = 16.sp, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Lugares visitados",
            fontSize = 18.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Minimapa OSM

    }
}
