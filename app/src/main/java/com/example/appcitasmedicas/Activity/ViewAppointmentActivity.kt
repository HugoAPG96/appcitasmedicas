package com.example.appcitasmedicas.Activity

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appcitasmedicas.Adapter.AppointmentAdapter
import com.example.appcitasmedicas.Domain.AppointmentModel
import com.example.appcitasmedicas.R
import com.google.firebase.database.*

class ViewAppointmentActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var doctorDatabase: DatabaseReference  // Nueva referencia para los médicos
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AppointmentAdapter
    private val appointmentList = mutableListOf<AppointmentModel>()
    private val doctorNameMap: MutableMap<String, String> = mutableMapOf() // Definimos explícitamente el tipo del mapa

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_appointment)

        // Inicializar referencia a la base de datos de citas
        database = FirebaseDatabase.getInstance().getReference("Appointment")
        // Inicializar referencia a la base de datos de médicos
        doctorDatabase = FirebaseDatabase.getInstance().getReference("Doctors")

        // Configurar el RecyclerView
        recyclerView = findViewById(R.id.viewRegisterCitas)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = AppointmentAdapter(appointmentList, doctorNameMap) // Pasamos el mapa con el tipo explícito
        recyclerView.adapter = adapter

        // Cargar las citas
        loadAppointments()

        // Botón para añadir nueva cita
        findViewById<Button>(R.id.buttonAddAppointment).setOnClickListener {
            // Finalizar la actividad actual y regresar al layout anterior
            finish()
        }

        findViewById<ImageView>(R.id.backBtn).setOnClickListener {
            onBackPressed() // Este método se encargará de ir atrás correctamente
        }

    }


    private fun loadAppointments() {
        // Escuchar una sola vez los datos de Firebase
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                appointmentList.clear() // Limpiar lista antes de agregar nuevos datos

                // Verificar si hay datos en Firebase
                if (snapshot.exists()) {
                    for (data in snapshot.children) {
                        val appointment = data.getValue(AppointmentModel::class.java)
                        appointment?.let {
                            // Agregar la cita a la lista
                            appointmentList.add(it)

                            // Obtener el nombre del médico basado en el DoctorId
                            getDoctorName(it.DoctorId.toString())  // Convertir DoctorId a String
                        }
                    }
                    // Notificar al adaptador que los datos han cambiado
                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(
                        this@ViewAppointmentActivity,
                        "No hay citas registradas.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@ViewAppointmentActivity,
                    "Error al cargar citas: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun getDoctorName(doctorId: String) {
        // Consultar el nombre del médico usando el DoctorId
        doctorDatabase.child(doctorId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Suponiendo que el médico tiene el campo "name"
                    val doctorName = snapshot.child("Name").value.toString()  // Obtenemos el campo "name"

                    // Guardar el nombre del médico en el mapa
                    doctorNameMap[doctorId] = doctorName

                    // Notificar al adaptador para que actualice la vista
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@ViewAppointmentActivity,
                    "Error al obtener el nombre del médico: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

}



