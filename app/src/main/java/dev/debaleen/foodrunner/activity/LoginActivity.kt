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
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import dev.debaleen.foodrunner.*
import dev.debaleen.foodrunner.util.*
import org.json.JSONObject

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
            navigateFromLoginActivity(LoginActivityDestinations.FORGOT_PASSWORD)
        }

        txtRegister.setOnClickListener {
            navigateFromLoginActivity(LoginActivityDestinations.REGISTRATION)
        }
    }

    private fun tryLogin() {
        val mobileNumber = etMobileNumber.text.toString()
        val password = etPassword.text.toString()

        when (checkValidInputs(mobileNumber, password)) {
            InputState.OKAY -> {
                sendNetworkRequest(mobileNumber, password)
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

    private fun saveToPreferences(
        userId: String,
        name: String,
        email: String,
        mobile: String,
        address: String
    ) {
        sharedPreferences.edit()
            .putBoolean(isLoggedInKey, true)
            .putString(userIdKey, userId)
            .putString(userNameKey, name)
            .putString(userMobileKey, mobile)
            .putString(userEmailKey, email)
            .putString(userAddressKey, address)
            .apply()
    }

    private fun sendNetworkRequest(mobileNumber: String, password: String) {
        if (ConnectionManager().checkConnectivity(this@LoginActivity)) {
            val queue = Volley.newRequestQueue(this@LoginActivity)

            val jsonParams = JSONObject()
            jsonParams.put("mobile_number", mobileNumber)
            jsonParams.put("password", password)

            val jsonRequest =
                object : JsonObjectRequest(Method.POST, LOGIN, jsonParams, Response.Listener {
                    try {
                        val returnObject = it.getJSONObject("data")
                        val success = returnObject.getBoolean("success")

                        if (success) {
                            val data = returnObject.getJSONObject("data")
                            val userId = data.getString("user_id")
                            val userName = data.getString("name")
                            val userEmail = data.getString("email")
                            val userMobile = data.getString("mobile_number")
                            val userAddress = data.getString("address")
                            saveToPreferences(userId, userName, userEmail, userMobile, userAddress)
                            navigateFromLoginActivity(LoginActivityDestinations.DASHBOARD)
                        } else {
                            val errorMessage = returnObject.getString("errorMessage")
                            Toast.makeText(applicationContext, errorMessage, Toast.LENGTH_SHORT)
                                .show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(applicationContext, e.localizedMessage, Toast.LENGTH_SHORT)
                            .show()
                    }
                }, Response.ErrorListener {
                    Toast.makeText(applicationContext, it.localizedMessage, Toast.LENGTH_SHORT)
                        .show()
                }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = TOKEN
                        return headers
                    }
                }
            queue.add(jsonRequest)
        } else {
            noInternetDialog(this@LoginActivity)
        }
    }

    private fun navigateFromLoginActivity(destination: LoginActivityDestinations) {
        val intent = when (destination) {
            LoginActivityDestinations.DASHBOARD -> Intent(
                this@LoginActivity,
                DashboardActivity::class.java
            )
            LoginActivityDestinations.FORGOT_PASSWORD -> Intent(
                this@LoginActivity,
                ForgotPasswordActivity::class.java
            )
            LoginActivityDestinations.REGISTRATION -> Intent(
                this@LoginActivity,
                RegistrationActivity::class.java
            )
        }
        startActivity(intent)
        finish()
    }
}