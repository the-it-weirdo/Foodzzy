package dev.debaleen.foodrunner.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.VolleyError
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import dev.debaleen.foodrunner.*
import dev.debaleen.foodrunner.network.NetworkTask
import dev.debaleen.foodrunner.util.*
import org.json.JSONObject

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var progressLayout: ProgressBar
    private lateinit var etMobileNumber: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var btnNext: Button

    private lateinit var networkTaskListener: NetworkTask.NetworkTaskListener

    private var mobileNumber: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        progressLayout = findViewById(R.id.progressLayout)
        progressLayout.hide()
        etMobileNumber = findViewById(R.id.etMobileNumber)
        etEmail = findViewById(R.id.etEmail)
        btnNext = findViewById(R.id.btnNext)

        btnNext.setOnClickListener {
            tryNext()
        }
    }

    private fun setupNetworkTaskListener() {
        networkTaskListener = object : NetworkTask.NetworkTaskListener {
            override fun onSuccess(result: JSONObject) {
                progressLayout.hide()
                btnNext.enable()
                try {
                    val returnObject = result.getJSONObject("data")
                    val success = returnObject.getBoolean("success")

                    if (success) {
                        val firstTry = returnObject.getBoolean("first_try")
                        val message = if (firstTry) {
                            "OTP has been sent to your mail."
                        } else {
                            "Please Refer to previous mail for OTP."
                        }
                        showInformationDialog(message)
                    } else {
                        val errorMessage = returnObject.getString("errorMessage")
                        showToast("$errorMessage. Please check your Email Id and Mobile number.")
                    }
                } catch (e: Exception) {
                    showToast("Error: ${e.localizedMessage}")
                }
            }

            override fun onFailed(error: VolleyError) {
                progressLayout.hide()
                showToast("Error: ${error.localizedMessage}")
            }
        }
    }

    private fun showInformationDialog(message: String) {
        val dialog = MaterialAlertDialogBuilder(this@ForgotPasswordActivity)
        dialog.setTitle("Information")
        dialog.setMessage(message)
        dialog.setPositiveButton("Okay") { _, _ ->
            navigateToResetPasswordActivity()
        }
        dialog.setCancelable(false)
        dialog.create()
        dialog.show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        navigateToLoginActivity()
    }

    private fun tryNext() {
        val mobile = etMobileNumber.text.toString()
        val email = etEmail.text.toString()

        when (checkValidInputs(mobile, email)) {
            InputState.INVALID_MOBILE -> {
                etMobileNumber.error = "Invalid Mobile Number"
            }
            InputState.INVALID_EMAIL -> {
                etEmail.error = "Invalid Email"
            }
            InputState.OKAY -> {
                sendNetworkRequest(mobile, email)
            }
            else -> {
                Toast.makeText(applicationContext, "Unknown InputState", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendNetworkRequest(mobile: String, email: String) {
        if (ConnectionManager().checkConnectivity(this@ForgotPasswordActivity)) {
            setupNetworkTaskListener()
            progressLayout.show()
            btnNext.disable()
            mobileNumber = mobile
            val jsonParam = JSONObject()
            jsonParam.put("mobile_number", mobile)
            jsonParam.put("email", email)
            NetworkTask(networkTaskListener).makeNetworkRequest(
                this@ForgotPasswordActivity, Request.Method.POST, FORGOT_PASSWORD, jsonParam
            )
        } else {
            noInternetDialog(this@ForgotPasswordActivity)
        }
    }

    private fun checkValidInputs(mobile: String, email: String): InputState {
        return when {
            mobile.trim().length != 10 -> {
                InputState.INVALID_MOBILE
            }
            !email.matches(emailRegex) -> {
                InputState.INVALID_EMAIL
            }
            else -> {
                InputState.OKAY
            }
        }
    }

    private fun navigateToResetPasswordActivity() {
        val intent = Intent(this@ForgotPasswordActivity, ResetPasswordActivity::class.java)
        intent.putExtra(userMobileKey, mobileNumber)
        startActivity(intent)
        ActivityCompat.finishAffinity(this@ForgotPasswordActivity)
    }

    private fun navigateToLoginActivity() {
        val intent = Intent(this@ForgotPasswordActivity, LoginActivity::class.java)
        startActivity(intent)
        ActivityCompat.finishAffinity(this@ForgotPasswordActivity)
    }
}