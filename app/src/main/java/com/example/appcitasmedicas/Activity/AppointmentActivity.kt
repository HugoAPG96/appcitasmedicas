package com.example.appcitasmedicas

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.appcitasmedicas.Activity.ViewAppointmentActivity
import com.example.appcitasmedicas.Domain.AppointmentModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import java.util.*

class AppointmentActivity : AppCompatActivity() {
    private lateinit var etDNI: EditText
    private lateinit var etNombre: EditText
    private lateinit var etApellidos: EditText
    private lateinit var etTelefono: EditText
    private lateinit var etEmail: EditText
    private lateinit var etFecha: EditText
    private lateinit var spHora: Spinner
    private lateinit var tvUsuario: TextView
    private lateinit var tvEspecialidad: TextView
    private lateinit var tvNombreMedico: TextView
    private lateinit var btnGuardar: Button
    private lateinit var btnCancelar: Button
    private lateinit var btnVisualizarCitas: Button

    private lateinit var datePickerDialog: DatePickerDialog
    private var doctorId: Int = 0
    private var especialidad: String? = null
    private var doctorName: String? = null

    private lateinit var mAuth: FirebaseAuth
    private lateinit var myRef: DatabaseReference
    private lateinit var idCounterRef: DatabaseReference
    private var nextAppointmentId: Int = 1 // Para almacenar el próximo ID disponible

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointment)

        // Inicializar vistas
        initializeViews()

        // Configurar el Spinner de la hora
        setupHourSpinner()

        etFecha.setOnClickListener { showDatePickerDialog() }

        // Inicializar Firebase
        mAuth = FirebaseAuth.getInstance()
        myRef = FirebaseDatabase.getInstance().reference
        idCounterRef = myRef.child("appointmentIdCounter")

        // Obtener datos del Intent
        setupIntentData()

        // Configurar botones
        btnGuardar.setOnClickListener { guardarCita() }
        btnCancelar.setOnClickListener {
            limpiarCampos()
            finish()
        }
        btnVisualizarCitas.setOnClickListener { visualizarCitas() }



        // Inicializar el contador de IDs si no existe
        idCounterRef.get().addOnSuccessListener { snapshot ->
            nextAppointmentId = (snapshot.value as? Int) ?: 1
            if (!snapshot.exists()) {
                idCounterRef.setValue(1)
            }
        }
    }

    private fun initializeViews() {
        etDNI = findViewById(R.id.etDNI)
        etNombre = findViewById(R.id.etNombre)
        etApellidos = findViewById(R.id.etApellidos)
        etTelefono = findViewById(R.id.etTelefono)
        etEmail = findViewById(R.id.etEmail)
        etFecha = findViewById(R.id.etFecha)
        spHora = findViewById(R.id.spHora)
        tvUsuario = findViewById(R.id.tvUsuario)
        tvEspecialidad = findViewById(R.id.tvEspecialidad)
        tvNombreMedico = findViewById(R.id.tvNombreMedico)
        btnGuardar = findViewById(R.id.btnGuardar)
        btnCancelar = findViewById(R.id.btnCancelar)
        btnVisualizarCitas = findViewById(R.id.btnVisualizarCitas)
    }

    private fun setupHourSpinner() {
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.horas_array, android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spHora.adapter = adapter
    }

    private fun setupIntentData() {
        doctorId = intent.getIntExtra("doctorId", 0)
        especialidad = intent.getStringExtra("especialidad")
        doctorName = intent.getStringExtra("doctorName")

        tvEspecialidad.text = especialidad
        tvNombreMedico.text = doctorName

        val userEmail = mAuth.currentUser?.email
        tvUsuario.text = userEmail
        etEmail.setText(userEmail)
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]
        val day = calendar[Calendar.DAY_OF_MONTH]

        datePickerDialog = DatePickerDialog(
            this,
            { _, year1, month1, dayOfMonth ->
                etFecha.setText("$dayOfMonth/${month1 + 1}/$year1")
            }, year, month, day
        )
        datePickerDialog.show()
    }

    private fun guardarCita() {
        val dni = etDNI.text.toString()
        val nombre = etNombre.text.toString()
        val apellidos = etApellidos.text.toString()
        val telefono = etTelefono.text.toString()
        val email = etEmail.text.toString()
        val fecha = etFecha.text.toString()
        val hora = spHora.selectedItem.toString()

        if (dni.isEmpty() || nombre.isEmpty() || apellidos.isEmpty() || telefono.isEmpty() || email.isEmpty() || fecha.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        // Usar una transacción para garantizar la consistencia del ID
        idCounterRef.runTransaction(object : Transaction.Handler {
            override fun doTransaction(currentData: MutableData): Transaction.Result {
                var currentId = currentData.getValue(Int::class.java) ?: 0
                if (currentId == 0) currentId = 1 // Inicializar si es 0
                nextAppointmentId = currentId
                currentData.value = currentId + 1 // Incrementar el contador
                return Transaction.success(currentData)
            }

            override fun onComplete(
                error: DatabaseError?,
                committed: Boolean,
                currentData: DataSnapshot?
            ) {
                if (error != null) {
                    Toast.makeText(this@AppointmentActivity, "Error al generar el ID", Toast.LENGTH_SHORT).show()
                    return
                }

                // Crear un mapa para almacenar los datos con las claves correctas
                val appointmentMap = mapOf(
                    "AppointmentId" to nextAppointmentId,
                    "DoctorId" to doctorId,
                    "Specialty" to (especialidad?.toString() ?: "General"),  // Asegurando que sea un String
                    "Date" to fecha,
                    "Time" to hora,
                    "DNI" to dni,
                    "FirstName" to nombre,
                    "LastName" to apellidos,
                    "Email" to email,
                    "Phone" to telefono,
                    "Status" to "Pending",
                    "UserEmail" to email
                )

                // Guardar la cita en la base de datos usando el mapa
                val appointmentRef = myRef.child("Appointment").child(nextAppointmentId.toString())
                appointmentRef.setValue(appointmentMap).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this@AppointmentActivity, "Cita guardada exitosamente", Toast.LENGTH_SHORT).show()
                        limpiarCampos()
                    } else {
                        Toast.makeText(this@AppointmentActivity, "Error al guardar la cita", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }


    private fun limpiarCampos() {
        etDNI.setText("")
        etNombre.setText("")
        etApellidos.setText("")
        etTelefono.setText("")
        etEmail.setText("")
        etFecha.setText("")
        spHora.setSelection(0)
    }

    private fun visualizarCitas() {
        // Navegar a la actividad ViewAppointmentActivity
            val intent = Intent(this, ViewAppointmentActivity::class.java)
            startActivity(intent)
    }

}

