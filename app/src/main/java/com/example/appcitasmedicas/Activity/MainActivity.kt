package com.example.appcitasmedicas.Activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appcitasmedicas.Adapter.CategoryAdapter
import com.example.appcitasmedicas.Adapter.TopDoctorAdapter
import com.example.appcitasmedicas.HomeActivity
import com.example.appcitasmedicas.R
import com.example.appcitasmedicas.ViewModel.MainViewModel
import com.example.appcitasmedicas.databinding.ActivityMainBinding

class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel = MainViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initCategory()
        initTopDoctors()

        val prefs = getSharedPreferences(getString(R.string.prefs_file), MODE_PRIVATE)
        val email = prefs.getString("email", "Email no disponible")

        // Muestra el email en un TextView
        val emailTextView = findViewById<TextView>(R.id.userTxt)
        emailTextView.text = email

    }

    private fun initTopDoctors() {
        binding.apply {
            progressBarTopDoctor.visibility = View.VISIBLE

            viewModel.doctors.observe(this@MainActivity) {
                recyclerViewTopDoctor.layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
                recyclerViewTopDoctor.adapter = TopDoctorAdapter(it)
                progressBarTopDoctor.visibility = View.GONE
            }
            viewModel.loadDoctors()

            doctorListTxt.setOnClickListener{
                startActivity(Intent(this@MainActivity,TopDoctorsActivity::class.java))
            }
        }
    }

    private fun initCategory() {
        binding.progressBarCategory.visibility = View.VISIBLE
        viewModel.category.observe(this, Observer {
            binding.viewCategory.layoutManager =
                LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
            binding.viewCategory.adapter = CategoryAdapter(it)
            binding.progressBarCategory.visibility = View.GONE
        })
        viewModel.loadCategory()
    }
}
