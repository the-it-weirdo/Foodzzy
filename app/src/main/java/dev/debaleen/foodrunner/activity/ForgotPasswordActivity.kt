package dev.debaleen.foodrunner.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import dev.debaleen.foodrunner.*
import dev.debaleen.foodrunner.util.*

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var etMobileNumber: EditText
    private lateinit var etEmail: EditText
    private lateinit var btnNext: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        etMobileNumber = findViewById(R.id.etMobileNumber)
        etEmail = findViewById(R.id.etEmail)
        btnNext = findViewById(R.id.btnNext)

        btnNext.setOnClickListener {
            tryNext()
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this@ForgotPasswordActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun tryNext() {
        val mobile = etMobileNumber.text.toString()
        val email = etEmail.text.toString()

        when (checkValidInputs(mobile, email)) {
            InputState.WRONG_MOBILE -> {
                etMobileNumber.error = "Invalid Mobile Number"
            }
            InputState.WRONG_EMAIL -> {
                etEmail.error = "Invalid Email"
            }
            InputState.OKAY -> {
                navigateToDashboard(mobile, email)
            }
            else -> {
                Toast.makeText(applicationContext, "Unknown InputState", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkValidInputs(mobile: String, email: String): InputState {
        return when {
            mobile.trim().length != 10 -> {
                InputState.WRONG_MOBILE
            }
            !email.matches(emailRegex) -> {
                InputState.WRONG_EMAIL
            }
            else -> {
                InputState.OKAY
            }
        }
    }

    private fun navigateToDashboard(mobile: String, email: String) {
        val intent = Intent(this@ForgotPasswordActivity, DashboardActivity::class.java)
        intent.putExtra(
            fromKey,
            forgotPasswordActivity
        )
        intent.putExtra(userMobileKey, mobile)
        intent.putExtra(userEmailKey, email)
        startActivity(intent)
        finish()
    }
}