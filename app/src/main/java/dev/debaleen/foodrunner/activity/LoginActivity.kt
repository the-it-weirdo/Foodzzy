package dev.debaleen.foodrunner.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import dev.debaleen.foodrunner.*
import dev.debaleen.foodrunner.util.*

class LoginActivity : AppCompatActivity() {

    private lateinit var etMobileNumber: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var txtForgotPassword: TextView
    private lateinit var txtRegister: TextView

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        sharedPreferences =
            getSharedPreferences(getString(R.string.preferences_file_name), Context.MODE_PRIVATE)
        setContentView(R.layout.activity_login)

        etMobileNumber = findViewById(R.id.etMobileNumber)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        txtForgotPassword = findViewById(R.id.txtForgotPassword)
        txtRegister = findViewById(R.id.txtRegister)



        btnLogin.setOnClickListener {
            tryLogin()
        }


        txtForgotPassword.setOnClickListener {
            val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
            startActivity(intent)
            finish()
        }

        txtRegister.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegistrationActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun tryLogin() {
        val mobileNumber = etMobileNumber.text.toString()
        val password = etPassword.text.toString()

        when (checkValidInputs(mobileNumber, password)) {
            InputState.OKAY -> {
                savePreferences(mobileNumber)
                val intent = Intent(this@LoginActivity, DashboardActivity::class.java)
                intent.putExtra(
                    fromKey,
                    loginActivity
                )
                intent.putExtra(userMobileKey, mobileNumber)
                intent.putExtra(userPasswordKey, password)
                startActivity(intent)
                finish()
            }
            InputState.WRONG_PASSWORD -> {
                etPassword.error = "Wrong Password"
            }
            InputState.WRONG_MOBILE -> {
                etMobileNumber.error = "Wrong mobile."
            }
            else -> {
                Toast.makeText(applicationContext, "Unknown InputState", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkValidInputs(mobileNumber: String, password: String): InputState {
        return when {
            mobileNumber.trim().length != 10 -> {
                InputState.WRONG_MOBILE
            }
            password.trim().length < 4 -> {
                InputState.WRONG_PASSWORD
            }
            else -> {
                InputState.OKAY
            }
        }
    }

    private fun savePreferences(mobileNumber: String) {
        sharedPreferences.edit()
            .putBoolean(isLoggedInKey, true)
            .putString(userMobileKey, mobileNumber)
            .apply()
    }
}