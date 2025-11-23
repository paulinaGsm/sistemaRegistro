
package com.uaa.registro_uaa

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentSnapshot



class EstudianteAdapter (
    private val listaDocumentos: List<DocumentSnapshot>,
    private val onDescargarClick: (DocumentSnapshot) -> Unit):RecyclerView.Adapter<EstudianteAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        val btnDescargar: Button = itemView.findViewById(R.id.btnDescargar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_estudiante, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = listaDocumentos.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val doc = listaDocumentos[position]
        val nombre = doc.get("Descriptivos de la persona.Nombre") ?: "Sin nombre"
        holder.tvNombre.text = nombre.toString()

        holder.btnDescargar.setOnClickListener {
            onDescargarClick(doc)
        }
    }


}