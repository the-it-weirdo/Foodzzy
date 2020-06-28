package dev.debaleen.foodrunner.activity

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.VolleyError
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import dev.debaleen.foodrunner.R
import dev.debaleen.foodrunner.network.NetworkTask
import dev.debaleen.foodrunner.util.*
import org.json.JSONObject

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var progressLayout: ProgressBar
    private lateinit var etOtp: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etConfirmPassword: TextInputEditText
    private lateinit var btnResetPassword: Button

    private lateinit var userMobileNumber: String
    private lateinit var networkTaskListener: NetworkTask.NetworkTaskListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = loadSharedPreferences()
        setContentView(R.layout.activity_reset_password)

        if (intent != null) {
            userMobileNumber = intent.getStringExtra(userMobileKey) ?: ""
        }
        progressLayout = findViewById(R.id.progressLayout)
        progressLayout.hide()
        etOtp = findViewById(R.id.etOtp)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnResetPassword = findViewById(R.id.btnResetPassword)

        btnResetPassword.setOnClickListener {
            tryReset()
        }
    }

    private fun tryReset() {
        val otp = etOtp.text.toString()
        val password = etPassword.text.toString()
        val confirmPassword = etConfirmPassword.text.toString()
        when (checkInputs(otp, password, confirmPassword)) {
            InputState.INVALID_OTP -> {
                etOtp.error = "Invalid OTP."
            }
            InputState.INVALID_PASSWORD -> {
                etPassword.error = "Password should contain minimum 6 characters."
            }
            InputState.PSW_NO_MATCH -> {
                etConfirmPassword.error = "Passwords do not match!"
            }
            InputState.OKAY -> {
                sendNetworkRequest(password, otp)
            }
            else -> {
                showToast("Unknown Input State.")
            }
        }
    }

    private fun checkInputs(otp: String, password: String, confirmPassword: String): InputState {
        return when {
            otp.length < 4 -> {
                InputState.INVALID_OTP
            }
            password.length < 6 -> {
                InputState.INVALID_PASSWORD
            }
            password.compareTo(confirmPassword) != 0 -> {
                InputState.PSW_NO_MATCH
            }
            else -> {
                InputState.OKAY
            }
        }
    }

    private fun setupNetworkTaskListener() {
        networkTaskListener =
            object : NetworkTask.NetworkTaskListener {
                override fun onSuccess(result: JSONObject) {
                    progressLayout.hide()
                    try {
                        val returnObject = result.getJSONObject("data")
                        val success = returnObject.getBoolean("success")

                        if (success) {
                            clearSharedPreferences()
                            val successMessage = returnObject.getString("successMessage")
                            showInformationDialog(successMessage)
                        } else {
                            btnResetPassword.enable()
                            val errorMessage = returnObject.getString("errorMessage")
                            showToast(errorMessage)
                        }
                    } catch (error: Exception) {
                        btnResetPassword.enable()
                        showToast("Error: ${error.localizedMessage}")
                    }
                }

                override fun onFailed(error: VolleyError) {
                    progressLayout.hide()
                    btnResetPassword.enable()
                    showToast("Error: ${error.localizedMessage}")
                }
            }
    }

    private fun showInformationDialog(message: String) {
        val dialog = MaterialAlertDialogBuilder(this@ResetPasswordActivity)
        dialog.setTitle("Information")
        dialog.setMessage(message)
        dialog.setPositiveButton("Okay") { _, _ ->
            navigateToLoginActivity()
        }
        dialog.setCancelable(false)
        dialog.create()
        dialog.show()
    }

    private fun sendNetworkRequest(password: String, otp: String) {
        if (ConnectionManager().checkConnectivity(this@ResetPasswordActivity)) {
            setupNetworkTaskListener()
            progressLayout.show()
            btnResetPassword.disable()

            val jsonParams = JSONObject()
            jsonParams.put("mobile_number", userMobileNumber)
            jsonParams.put("password", password)
            jsonParams.put("otp", otp)

            NetworkTask(networkTaskListener).makeNetworkRequest(
                this@ResetPasswordActivity, Request.Method.POST, RESET_PASSWORD, jsonParams
            )
        } else {
            noInternetDialog(this@ResetPasswordActivity)
        }
    }

    private fun clearSharedPreferences() {
        sharedPreferences.edit().clear().apply()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        navigateToLoginActivity()
    }

    private fun navigateToLoginActivity() {
        val intent = Intent(this@ResetPasswordActivity, LoginActivity::class.java)
        startActivity(intent)
        ActivityCompat.finishAffinity(this@ResetPasswordActivity)
    }
}