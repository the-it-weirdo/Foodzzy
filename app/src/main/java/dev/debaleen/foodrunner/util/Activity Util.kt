package dev.debaleen.foodrunner.util

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import dev.debaleen.foodrunner.R

fun Activity.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(applicationContext, message, duration).show()
}

fun Activity.loadSharedPreferences(): SharedPreferences {
    return getSharedPreferences(getString(R.string.preferences_file_name), Context.MODE_PRIVATE)
}