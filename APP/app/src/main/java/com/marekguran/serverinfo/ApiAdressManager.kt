package com.marekguran.serverinfo

import android.content.Context
import android.content.SharedPreferences

class ApiAddressManager(context: Context) {

    companion object {
        private const val PREFS_NAME = "ApiAddressPrefs"
        private const val KEY_API_ADDRESS = ""

        // Default API address
        private const val DEFAULT_API_ADDRESS = "http://10.0.1.1:9002/api"
    }

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // Function to save the API address
    fun saveApiAddress(apiAddress: String) {
        sharedPreferences.edit().putString(KEY_API_ADDRESS, apiAddress).apply()
    }

    // Function to retrieve the API address with the default value
    fun getApiAddress(): String {
        val savedApiAddress = sharedPreferences.getString(KEY_API_ADDRESS, null)
        return if (!savedApiAddress.isNullOrEmpty()) savedApiAddress!! else DEFAULT_API_ADDRESS
    }
}
