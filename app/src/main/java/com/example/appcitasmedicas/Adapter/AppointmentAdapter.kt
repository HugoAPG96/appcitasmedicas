package com.example.appcitasmedicas.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appcitasmedicas.Domain.AppointmentModel
import com.example.appcitasmedicas.R

class AppointmentAdapter(
    private val appointmentList: List<AppointmentModel>,
    private val doctorNameMap: Map<String, String> // Recibir el mapa de nombres de médicos con el tipo explícito
) : RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.viewholder_appointment, parent, false)
        return AppointmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        val appointment: AppointmentModel = appointmentList[position]

        // Usar el mapa para obtener el nombre del médico con el DoctorId convertido a String
        val doctorName = doctorNameMap[appointment.DoctorId.toString()] ?: "Desconocido"

        // Mostrar los datos de la cita
        holder.tvUsuarioDni.text = "Nombre: ${appointment.FirstName} ${appointment.LastName} - DNI: ${appointment.DNI}"
        holder.tvMedicoEspecialidad.text = "Médico: $doctorName | Especialidad: ${appointment.Specialty}"
        holder.tvFechaHora.text = "Fecha: ${appointment.Date} | Hora: ${appointment.Time}"
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

