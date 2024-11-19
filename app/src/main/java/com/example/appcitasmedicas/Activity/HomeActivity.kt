package com.example.appcitasmedicas

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.appcitasmedicas.Activity.MainActivity
import com.example.appcitasmedicas.databinding.ActivityIntroBinding
import com.google.firebase.auth.FirebaseAuth
import com.facebook.login.LoginManager

enum class ProviderType {
    BASIC,
    GOOGLE,
    FACEBOOK
}

class HomeActivity : AppCompatActivity() {

    // Declaración de las vistas
    private lateinit var emailTextView: TextView
    private lateinit var providerTextView: TextView
    private lateinit var logOutButton: Button
    private lateinit var startButton: Button // El botón de "Empezar"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro) // Asegúrate de que este layout tenga el botón de "Empezar"

        // Inicialización de las vistas
        emailTextView = findViewById(R.id.emailTextView)
        providerTextView = findViewById(R.id.providerTextView)
        logOutButton = findViewById(R.id.logOutButton)
        startButton = findViewById(R.id.startBtn) // Aquí inicializas el botón de "Empezar"

        // Acción al presionar el botón de "Empezar"
        startButton.setOnClickListener {
            startActivity(Intent(this@HomeActivity, MainActivity::class.java)) // Inicia la MainActivity
        }

        // Inicialización de las vistas del usuario
        val bundle = intent.extras
        val email = bundle?.getString("email")
        val provider = bundle?.getString("provider")

        // Llamada a la función setup después de inicializar las vistas
        setup(email ?: "", provider ?: "")

        // Guardado de datos
        val prefs = getSharedPreferences(getString(R.string.prefs_file), MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("provider", provider)
        prefs.apply()


    }

    private fun setup(email: String, provider: String) {
        title = "Inicio"
        emailTextView.text = email
        providerTextView.text = provider

        logOutButton.setOnClickListener {
            // Borrado de datos
            val prefs = getSharedPreferences(getString(R.string.prefs_file), MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()

            if (provider == ProviderType.FACEBOOK.name) {
                LoginManager.getInstance().logOut()
            }

            FirebaseAuth.getInstance().signOut()
            onBackPressedDispatcher.onBackPressed()
        }
    }



}
