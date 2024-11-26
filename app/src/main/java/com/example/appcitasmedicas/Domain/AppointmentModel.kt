package com.example.appcitasmedicas.Domain

import com.google.firebase.database.PropertyName

data class AppointmentModel(
    @get:PropertyName("appointmentId")
    val AppointmentId: Int = 0,

    @get:PropertyName("doctorId")
    val DoctorId: Int = 0,

    @get:PropertyName("specialty")
    val Specialty: String = "",

    @get:PropertyName("date")
    val Date: String = "",

    @get:PropertyName("time")
    val Time: String = "",

    @get:PropertyName("dni")
    val DNI: String = "",

    @get:PropertyName("firstName")
    val FirstName: String = "",

    @get:PropertyName("lastName")
    val LastName: String = "",

    @get:PropertyName("email")
    val Email: String = "",

    @get:PropertyName("phone")
    val Phone: String = "",

    @get:PropertyName("status")
    val Status: String = "", // 'Pending', 'Confirmed', 'Cancelled'

    @get:PropertyName("userEmail")
    val UserEmail: String = ""
)
