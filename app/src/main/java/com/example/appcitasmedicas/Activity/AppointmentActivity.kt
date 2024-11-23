package com.example.appcitasmedicas

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

class AppointmentActivity : AppCompatActivity() {
    private var etDNI: EditText? = null
    private var etNombre: EditText? = null
    private var etApellidos: EditText? = null
    private var etTelefono: EditText? = null
    private var etEmail: EditText? = null
    private var etFecha: EditText? = null
    private var spHora: Spinner? = null
    private var tvUsuario: TextView? = null
    private var tvEspecialidad: TextView? = null
    private var tvNombreMedico: TextView? = null
    private var btnGuardar: Button? = null
    private var btnCancelar: Button? = null
    private var btnVisualizarCitas: Button? = null
    private var datePickerDialog: DatePickerDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointment)

        // Inicializar vistas
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

        // Configurar el Spinner de la hora
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.horas_array, android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spHora?.adapter = adapter

        // Configuración para la selección de la fecha
        etFecha?.setOnClickListener { showDatePickerDialog() }

        // Guardar la cita
        btnGuardar?.setOnClickListener { guardarCita() }

        // Cancelar la cita
        btnCancelar?.setOnClickListener { limpiarCampos() }

        // Visualizar citas
        btnVisualizarCitas?.setOnClickListener { visualizarCitas() }
    }

    // Mostrar el dialogo de fecha
    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]
        val day = calendar[Calendar.DAY_OF_MONTH]

        datePickerDialog = DatePickerDialog(
            this,
            { _, year1, month1, dayOfMonth ->
                etFecha?.setText("$dayOfMonth/${month1 + 1}/$year1")
            }, year, month, day
        )
        datePickerDialog?.show()
    }

    // Lógica para guardar la cita
    private fun guardarCita() {
        val dni = etDNI?.text.toString()
        val nombre = etNombre?.text.toString()
        val apellidos = etApellidos?.text.toString()
        val telefono = etTelefono?.text.toString()
        val email = etEmail?.text.toString()
        val fecha = etFecha?.text.toString()
        val hora = spHora?.selectedItem.toString()

        // Validar que los campos no estén vacíos
        if (dni.isEmpty() || nombre.isEmpty() || apellidos.isEmpty() || telefono.isEmpty() || email.isEmpty() || fecha.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
        } else {
            // Aquí iría el código para guardar la cita en la base de datos
            // Mostrar mensaje de éxito
            Toast.makeText(this, "Cita guardada exitosamente", Toast.LENGTH_SHORT).show()
        }
    }

    // Lógica para limpiar los campos
    private fun limpiarCampos() {
        etDNI?.setText("")
        etNombre?.setText("")
        etApellidos?.setText("")
        etTelefono?.setText("")
        etEmail?.setText("")
        etFecha?.setText("")
        spHora?.setSelection(0)
    }

    // Lógica para visualizar las citas
    private fun visualizarCitas() {
        // Aquí puedes mostrar una nueva actividad o un fragmento con la lista de citas
        // Ejemplo: Intent para abrir la actividad VisualizarCitasActivity
    }
}
