package com.example.appcitasmedicas.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appcitasmedicas.Domain.AppointmentModel
import com.example.appcitasmedicas.R

class AppointmentAdapter(private val appointmentList: List<AppointmentModel>) :
    RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.viewholder_appointment, parent, false)
        return AppointmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        val appointment: AppointmentModel = appointmentList[position]

        // Usando las propiedades del modelo directamente
        holder.tvUsuarioDni.text = "Usuario: ${appointment.firstName} ${appointment.lastName} - DNI: ${appointment.dni}"
        holder.tvMedicoEspecialidad.text = "Médico: ${appointment.firstName} ${appointment.lastName} | Especialidad: ${appointment.specialty}"
        holder.tvFechaHora.text = "Fecha: ${appointment.date} | Hora: ${appointment.time}"
    }

    override fun getItemCount(): Int {
        return appointmentList.size
    }

    class AppointmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvUsuarioDni: TextView = itemView.findViewById(R.id.tvUsuarioDni)
        var tvMedicoEspecialidad: TextView = itemView.findViewById(R.id.tvMedicoEspecialidad)
        var tvFechaHora: TextView = itemView.findViewById(R.id.tvFechaHora)
    }
}
