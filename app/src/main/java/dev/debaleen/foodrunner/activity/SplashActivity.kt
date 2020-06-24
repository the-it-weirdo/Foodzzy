package dev.debaleen.foodrunner.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import dev.debaleen.foodrunner.R
import dev.debaleen.foodrunner.util.fromKey
import dev.debaleen.foodrunner.util.isLoggedInKey
import dev.debaleen.foodrunner.util.splashActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences =
            getSharedPreferences(getString(R.string.preferences_file_name), Context.MODE_PRIVATE)
        setContentView(R.layout.activity_splash)
        val isLoggedIn = sharedPreferences.getBoolean(isLoggedInKey, false)

        Handler().postDelayed({
            navigateFromSplash(isLoggedIn)
        }, 2000)
    }

    private fun navigateFromSplash(isLoggedIn: Boolean) {
        val intentFromSplash: Intent
        if (isLoggedIn) {
            intentFromSplash = Intent(this@SplashActivity, DashboardActivity::class.java)
            intentFromSplash.putExtra(
                fromKey,
                splashActivity
            )
        } else {
            intentFromSplash = Intent(this@SplashActivity, LoginActivity::class.java)
        }

        startActivity(intentFromSplash)
        finish()
    }
}