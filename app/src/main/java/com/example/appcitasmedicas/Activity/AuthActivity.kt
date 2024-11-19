package com.example.appcitasmedicas

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.appcitasmedicas.ProviderType
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class AuthActivity : AppCompatActivity() {

    private val GOOGLE_SIGN_IN = 100
    private val callbackManager = CallbackManager.Factory.create()
    private lateinit var authLayout: LinearLayout

    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var signUpButton: Button
    private lateinit var logInButton: Button
    private lateinit var googleButton: ImageButton
    private lateinit var facebookButton: ImageButton
    private lateinit var rememberUserCheckBox: CheckBox
    // Variables para protección contra fuerza bruta
    private lateinit var sharedPrefs: SharedPreferences
    private var failedAttempts = 0
    private val maxFailedAttempts = 3
    private val lockoutDurationMillis = 2000L  // 1 minuto de bloqueo
    private var isLockedOut = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Base_Theme_AppCitasMedicas)
        setContentView(R.layout.activity_auth)

        // Inicializa las vistas
        authLayout = findViewById(R.id.authLayout)
        email = findViewById(R.id.emailEditText)
        password = findViewById(R.id.passwordEditText)
        signUpButton = findViewById(R.id.signUpButton)
        logInButton = findViewById(R.id.loginButton)
        googleButton = findViewById(R.id.googleImageButton)
        facebookButton = findViewById(R.id.facebookButton)
        rememberUserCheckBox = findViewById(R.id.rememberUserCheckBox)

        // Inicializar SharedPreferences
        sharedPrefs = getSharedPreferences(getString(R.string.prefs_file), MODE_PRIVATE)

        // Leer intentos fallidos, estado de bloqueo y correo electrónico recordado
        failedAttempts = sharedPrefs.getInt("failedAttempts", 0)
        isLockedOut = sharedPrefs.getBoolean("isLockedOut", false)

        // Si el correo está guardado, se llena el campo de email
        val savedEmail = sharedPrefs.getString("savedEmail", "")
        if (savedEmail != null && savedEmail.isNotEmpty()) {
            email.setText(savedEmail)
            rememberUserCheckBox.isChecked = true  // Marcar la opción de "Recordar usuario"
        }

        // Verificar si está bloqueado al iniciar
        checkLockoutState()

        // Ajuste de las vistas
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Evento de Google Analytics
        val analytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("message", "Integración de Firebase completa")
        analytics.logEvent("InitScreen", bundle)

        // Configurar botones y sesión
        setup()
        session()
    }

    override fun onStart() {
        super.onStart()
        authLayout.visibility = View.VISIBLE
    }

    private fun session() {
        val prefs = getSharedPreferences(getString(R.string.prefs_file), MODE_PRIVATE)
        val email = prefs.getString("email", null)
        val provider = prefs.getString("provider", null)

        if (email != null && provider != null) {
            authLayout.visibility = View.INVISIBLE
            showHome(email, ProviderType.valueOf(provider))
        }
    }

    private fun setup() {
        title = "Autenticación"

        // Registrar usuario
        signUpButton.setOnClickListener {
            if (!isLockedOut) {
                if (email.text.isNotEmpty() && password.text.isNotEmpty()) {
                    FirebaseAuth.getInstance()
                        .createUserWithEmailAndPassword(
                            email.text.toString(),
                            password.text.toString()
                        ).addOnCompleteListener {
                            if (it.isSuccessful) {
                                saveEmailIfChecked()  // Guardar correo si la opción está activada
                                resetFailedAttempts()
                                showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
                            } else {
                                handleFailedAttempt()
                                showAlert()
                            }
                        }
                }
            } else {
                showLockoutAlert()
            }
        }

        // Acceder con usuario registrado
        logInButton.setOnClickListener {
            if (!isLockedOut) {
                if (email.text.isNotEmpty() && password.text.isNotEmpty()) {
                    FirebaseAuth.getInstance()
                        .signInWithEmailAndPassword(
                            email.text.toString(),
                            password.text.toString()
                        ).addOnCompleteListener {
                            if (it.isSuccessful) {
                                saveEmailIfChecked()  // Guardar correo si la opción está activada
                                resetFailedAttempts()
                                showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
                            } else {
                                handleFailedAttempt()
                                showAlert()
                            }
                        }
                }
            } else {
                showLockoutAlert()
            }
        }

        // Iniciar sesión con Google
        googleButton.setOnClickListener {
            if (!isLockedOut) {
                val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()

                val googleClient = GoogleSignIn.getClient(this, googleConf)
                googleClient.signOut()

                // Iniciar la actividad de Google Sign-In
                startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)
            } else {
                showLockoutAlert()
            }
        }

        // Iniciar sesión con Facebook (similar a Google)
        facebookButton.setOnClickListener {
            if (!isLockedOut) {
                try {
                    LoginManager.getInstance().logInWithReadPermissions(this, listOf("email"))
                    LoginManager.getInstance().registerCallback(callbackManager,
                        object : FacebookCallback<LoginResult> {
                            override fun onSuccess(result: LoginResult) {
                                val token = result.accessToken
                                val credential = FacebookAuthProvider.getCredential(token.token)

                                FirebaseAuth.getInstance().signInWithCredential(credential)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            saveEmailIfChecked()  // Guardar correo si la opción está activada
                                            resetFailedAttempts()
                                            showHome(task.result?.user?.email ?: "", ProviderType.FACEBOOK)
                                        } else {
                                            handleFailedAttempt()
                                            showAlert()
                                        }
                                    }
                            }

                            override fun onCancel() {
                                // Manejar la cancelación del login de Facebook
                            }

                            override fun onError(error: FacebookException) {
                                handleFailedAttempt()
                                showAlert()
                            }
                        })
                } catch (e: Exception) {
                    handleFailedAttempt()
                    showAlert()
                }
            } else {
                showLockoutAlert()
            }
        }
    }

    private fun saveEmailIfChecked() {
        // Guardar el correo electrónico si el CheckBox está marcado
        if (rememberUserCheckBox.isChecked) {
            sharedPrefs.edit().putString("savedEmail", email.text.toString()).apply()
        } else {
            // Si el CheckBox no está marcado, remover el correo guardado
            sharedPrefs.edit().remove("savedEmail").apply()
        }
    }

    private fun handleFailedAttempt() {
        failedAttempts++
        sharedPrefs.edit().putInt("failedAttempts", failedAttempts).apply()

        if (failedAttempts >= maxFailedAttempts) {
            lockUserOut()
        }
    }

    private fun lockUserOut() {
        isLockedOut = true
        sharedPrefs.edit().putBoolean("isLockedOut", true).apply()
        object : CountDownTimer(lockoutDurationMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // Mostrar un mensaje de bloqueo si es necesario
            }

            override fun onFinish() {
                resetFailedAttempts()
                isLockedOut = false
                sharedPrefs.edit().putBoolean("isLockedOut", false).apply()
            }
        }.start()
    }

    private fun resetFailedAttempts() {
        failedAttempts = 0
        sharedPrefs.edit().putInt("failedAttempts", 0).apply()
    }

    private fun checkLockoutState() {
        if (isLockedOut) {
            lockUserOut()
        }
    }

    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando al usuario")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showLockoutAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Bloqueado")
        builder.setMessage("Has superado el número de intentos fallidos. Intenta de nuevo más tarde.")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showHome(email: String, provider: ProviderType) {
        val homeIntent = Intent(this, HomeActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(homeIntent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                resetFailedAttempts()
                                showHome(account.email ?: "", ProviderType.GOOGLE)
                            } else {
                                handleFailedAttempt()
                                showAlert()
                            }
                        }
                }
            } catch (e: ApiException) {
                handleFailedAttempt()
                showAlert()
            }
        }
    }
}
