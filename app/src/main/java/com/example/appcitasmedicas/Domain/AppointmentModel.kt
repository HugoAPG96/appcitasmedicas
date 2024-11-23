package com.example.appcitasmedicas.Domain

data class AppointmentModel(
    val appointmentId: Int,
    val doctorId: Int,
    val specialty: String,
    val date: String,
    val time: String,
    val dni: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val status: String, // 'Pending', 'Confirmed', 'Cancelled'
    val userEmail: String
)
