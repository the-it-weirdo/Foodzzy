package dev.debaleen.foodrunner.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import dev.debaleen.foodrunner.*
import dev.debaleen.foodrunner.util.*

class RegistrationActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etMobileNumber: EditText
    private lateinit var etDeliveryAddress: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnRegister: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences =
            getSharedPreferences(getString(R.string.preferences_file_name), Context.MODE_PRIVATE)

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

    private fun navigateToLoginActivity() {
        val intent = Intent(this@RegistrationActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
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
                navigateToDashboardActivity(name, email, mobile, address, password)
            }
        }

    }

    private fun checkValidInputs(
        name: String,
        email: String,
        mobile: String,
        address: String,
        password: String,
        confirmPassword: String
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

    private fun navigateToDashboardActivity(
        name: String,
        email: String,
        mobile: String,
        address: String,
        password: String
    ) {
        // Just for showing what was entered. Not setting Login status to true.
        saveToPreferences(name, email, mobile, address)
        val intent = Intent(this@RegistrationActivity, DashboardActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun saveToPreferences(
        name: String,
        email: String,
        mobile: String,
        address: String
    ) {
        sharedPreferences.edit()
            .putBoolean(isLoggedInKey, true)
            .putString(userNameKey, name)
            .putString(userMobileKey, mobile)
            .putString(userEmailKey, email)
            .putString(userAddressKey, address)
            .apply()
    }
}