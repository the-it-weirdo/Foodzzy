package dev.debaleen.foodrunner.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.provider.Settings
import androidx.core.app.ActivityCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ConnectionManager {

    fun checkConnectivity(context: Context): Boolean {

        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo

        return if (activeNetwork?.isConnected != null) {
            activeNetwork.isConnected
        } else {
            false
        }
    }
}

fun noInternetDialog(context: Context) {
    val dialog = MaterialAlertDialogBuilder(context)
    dialog.setTitle("Error")
    dialog.setMessage("Internet connection not found.")

    dialog.setPositiveButton("Open Settings") { _, _ ->
        // Opens settings
        val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
        context.startActivity(settingsIntent)
        //(context as Activity).finish()
    }
    dialog.setNegativeButton("Exit App") { _, _ ->
        // Closes app safely
        ActivityCompat.finishAffinity(context as Activity)
    }
    dialog.create()
    dialog.show()
}