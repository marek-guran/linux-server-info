package com.marekguran.serverinfo

import android.content.Context
import android.content.SharedPreferences

class DarkMode(context: Context) {
    companion object {
        private const val PREFS_NAME = "DarkModePrefs"
        private const val KEY_DARK_MODE_ENABLED = "" // Set a valid key name
        // Set the default value as a string "false" (light theme)
        private const val DEFAULT_DARK_MODE_ENABLED = "false"
    }

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun isDarkModeEnabled(): Boolean {
        // Retrieve the value as a string, and convert it to a boolean
        val stringValue = sharedPreferences.getString(KEY_DARK_MODE_ENABLED, DEFAULT_DARK_MODE_ENABLED)
        return stringValue == "true"
    }

    fun setDarkModeEnabled(enabled: Boolean) {
        // Store the value as a string "true" or "false"
        sharedPreferences.edit().putString(KEY_DARK_MODE_ENABLED, enabled.toString()).apply()
    }
}
