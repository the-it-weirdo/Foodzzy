package dev.debaleen.foodrunner.activity

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import com.android.volley.Request
import com.android.volley.VolleyError
import dev.debaleen.foodrunner.*
import dev.debaleen.foodrunner.util.*
import org.json.JSONObject

class RegistrationActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etMobileNumber: EditText
    private lateinit var etDeliveryAddress: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnRegister: Button

    private lateinit var networkTaskListener: NetworkTask.NetworkTaskListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = loadSharedPreferences()

        setContentView(R.layout.activity_registration)
        title = "Register Yourself"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etMobileNumber = findViewById(R.id.etMobileNumber)
        etDeliveryAddress = findViewById(R.id.etDeliveryAddress)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)

        btnRegister.setOnClickListener {
            tryRegister()
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            navigateToLoginActivity()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        navigateToLoginActivity()
    }

    private fun tryRegister() {
        val name = etName.text.toString()
        val email = etEmail.text.toString()
        val mobile = etMobileNumber.text.toString()
        val address = etDeliveryAddress.text.toString()
        val password = etPassword.text.toString()
        val confirmPassword = etConfirmPassword.text.toString()
        when (checkValidInputs(name, email, mobile, address, password, confirmPassword)) {
            InputState.WRONG_NAME -> {
                etName.error = "Name should contain minimum 3 characters."
            }
            InputState.WRONG_EMAIL -> {
                etEmail.error = "Invalid Email."
            }
            InputState.WRONG_MOBILE -> {
                etMobileNumber.error = "Invalid mobile number."
            }
            InputState.WRONG_ADDRESS -> {
                etDeliveryAddress.error = "Field should not be empty."
            }
            InputState.WRONG_PASSWORD -> {
                etPassword.error = "Password should contain minimum 4 characters."
            }
            InputState.PSW_NO_MATCH -> {
                etConfirmPassword.error = "Passwords do not match!"
            }
            InputState.OKAY -> {
                sendNetworkRequest(name, mobile, password, address, email)
            }
        }

    }

    private fun checkValidInputs(
        name: String, email: String, mobile: String,
        address: String, password: String, confirmPassword: String
    ): InputState {
        return when {
            name.trim().length < 3 -> {
                InputState.WRONG_NAME
            }
            !email.matches(emailRegex) -> {
                InputState.WRONG_EMAIL
            }
            mobile.trim().length != 10 -> {
                InputState.WRONG_MOBILE
            }
            address.isEmpty() -> {
                InputState.WRONG_ADDRESS
            }
            password.length < 4 -> {
                InputState.WRONG_PASSWORD
            }
            password.compareTo(confirmPassword) != 0 -> {
                InputState.PSW_NO_MATCH
            }
            else -> {
                InputState.OKAY
            }
        }
    }

    private fun sendNetworkRequest(
        name: String, mobileNumber: String, password: String, address: String, email: String
    ) {
        if (ConnectionManager().checkConnectivity(this@RegistrationActivity)) {
            setupNetworkTaskListener()

            val jsonParams = JSONObject()
            jsonParams.put("name", name)
            jsonParams.put("mobile_number", mobileNumber)
            jsonParams.put("password", password)
            jsonParams.put("address", address)
            jsonParams.put("email", email)

            NetworkTask(networkTaskListener).makeNetworkRequest(
                this@RegistrationActivity, Request.Method.POST, REGISTER, jsonParams
            )
        } else {
            noInternetDialog(this@RegistrationActivity)
        }
    }

    private fun setupNetworkTaskListener() {
        networkTaskListener =
            object : NetworkTask.NetworkTaskListener {
                override fun onSuccess(result: JSONObject) {
                    try {
                        val returnObject = result.getJSONObject("data")
                        val success = returnObject.getBoolean("success")

                        if (success) {
                            val data = returnObject.getJSONObject("data")
                            val userId = data.getString("user_id")
                            val userName = data.getString("name")
                            val userEmail = data.getString("email")
                            val userMobileNumber = data.getString("mobile_number")
                            val userAddress = data.getString("address")
                            saveToPreferences(
                                userId, userName, userEmail, userMobileNumber, userAddress
                            )
                            navigateToDashboardActivity()
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

    private fun navigateToLoginActivity() {
        val intent = Intent(this@RegistrationActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToDashboardActivity() {
        val intent = Intent(this@RegistrationActivity, DashboardActivity::class.java)
        startActivity(intent)
        finish()
    }
}