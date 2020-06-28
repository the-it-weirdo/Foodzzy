package dev.debaleen.foodrunner.activity

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.VolleyError
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

    private lateinit var networkTaskListener: NetworkTask.NetworkTaskListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        sharedPreferences = loadSharedPreferences()

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

    private fun setupNetworkTaskListener() {
        networkTaskListener = object : NetworkTask.NetworkTaskListener {
            override fun onSuccess(result: JSONObject) {
                try {
                    val returnObject = result.getJSONObject("data")
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
                        showToast(errorMessage)
                    }
                } catch (e: Exception) {
                    showToast("Error: ${e.localizedMessage}")
                }
            }

            override fun onFailed(error: VolleyError) {
                showToast("Error: ${error.localizedMessage}")
            }
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
                showToast("Unknown Input State.")
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

    private fun sendNetworkRequest(mobileNumber: String, password: String) {
        if (ConnectionManager().checkConnectivity(this@LoginActivity)) {
            setupNetworkTaskListener()

            val jsonParams = JSONObject()
            jsonParams.put("mobile_number", mobileNumber)
            jsonParams.put("password", password)

            NetworkTask(networkTaskListener).makeNetworkRequest(
                this@LoginActivity, Request.Method.POST, LOGIN, jsonParams
            )
        } else {
            noInternetDialog(this@LoginActivity)
        }
    }

    private fun saveToPreferences(
        userId: String, name: String, email: String, mobile: String, address: String
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