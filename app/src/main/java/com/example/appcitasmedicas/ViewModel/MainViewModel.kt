package com.example.appcitasmedicas.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.example.appcitasmedicas.Domain.CategoryModel
import com.example.appcitasmedicas.Domain.DoctorsModel

class MainViewModel() : ViewModel() {

    private val firebaseDatabase = FirebaseDatabase.getInstance()

    private val _category = MutableLiveData<MutableList<CategoryModel>>()
    private val _doctors = MutableLiveData<MutableList<DoctorsModel>>()
    val category: LiveData<MutableList<CategoryModel>> = _category
    val doctors: LiveData<MutableList<DoctorsModel>> = _doctors

    fun loadCategory() {
        val ref = firebaseDatabase.getReference("Category")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists = mutableListOf<CategoryModel>()
                for (childSnapshot in snapshot.children) {
                    val list = childSnapshot.getValue(CategoryModel::class.java)
                    if (list != null) {
                        lists.add(list)
                    }
                }
                _category.value = lists
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    fun loadDoctors() {
        // Obtener una referencia a la base de datos en la ruta "Doctors"
        val ref = firebaseDatabase.getReference("Doctors")

        // Agregar un listener para escuchar cambios en los datos
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Crear una lista mutable para almacenar los datos de los médicos
                val lists = mutableListOf<DoctorsModel>()

                // Iterar sobre cada elemento hijo en la snapshot
                for (childSnapshot in snapshot.children) {
                    // Obtener el valor del elemento hijo y convertirlo a un objeto DoctorsModel
                    val list = childSnapshot.getValue(DoctorsModel::class.java)

                    // Si el objeto no es nulo, agregarlo a la lista
                    if (list != null) {
                        lists.add(list)
                    }
                }
                _doctors.value = lists
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}
