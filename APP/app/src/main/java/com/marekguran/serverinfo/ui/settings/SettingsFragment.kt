package com.marekguran.serverinfo.ui.settings

import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.switchmaterial.SwitchMaterial
import com.marekguran.serverinfo.ApiAddressManager
import com.marekguran.serverinfo.DarkMode
import com.marekguran.serverinfo.R
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class SettingsFragment : Fragment() {

    private fun getApi(): String {
        val apiAddressManager = ApiAddressManager(requireContext())
        return apiAddressManager.getApiAddress()
    }

    private val apiUrl: String
        get() = getApi()

    // Declare EditText and Button views
    private var apiAddressEditText: EditText? = null
    private var saveButton: Button? = null
    private var darkModeSwitch: SwitchMaterial? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        // Initialize EditText and Button views
        apiAddressEditText = view.findViewById(R.id.api_address_edit_text)
        saveButton = view.findViewById(R.id.save_btn)
        darkModeSwitch = view.findViewById(R.id.dark_mode_switch)
        val darkMode = DarkMode(requireContext())
        darkModeSwitch?.isChecked = darkMode.isDarkModeEnabled()

        darkModeSwitch?.setOnCheckedChangeListener { _, isChecked ->
            // Convert isChecked (true or false) to a boolean
            val isEnabled = isChecked
            darkMode.setDarkModeEnabled(isEnabled)
            requireActivity().recreate()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val apiAddressManager = ApiAddressManager(requireContext())

        // Fetch JSON data initially
        fetchDataAndUpdateViews()

        // Schedule periodic updates every second
        schedulePeriodicUpdates()

        // Set the current API address as a hint in the EditText
        apiAddressEditText?.hint = apiUrl

        apiAddressEditText?.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                apiAddressEditText?.hint = ""
            } else {
                // Restore the hint to the new API address if the EditText loses focus
                apiAddressEditText?.hint = apiUrl
            }
        }

        // Handle the save button click
        saveButton?.setOnClickListener {
            val newApiAddress = apiAddressEditText?.text?.toString()?.trim()

            if (TextUtils.isEmpty(newApiAddress)) {
                // Handle case where input is empty
                apiAddressEditText?.error = "API address cannot be empty"
            } else {
                // Save the new API address using ApiAddressManager
                if (newApiAddress != null) {
                    apiAddressManager.saveApiAddress(newApiAddress)
                    Toast.makeText(requireContext(), "API address saved.", Toast.LENGTH_LONG).show()
                }
                // Clear the EditText
                apiAddressEditText?.text?.clear()
                // Update the hint to the new API address
                apiAddressEditText?.hint = apiUrl
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    private fun fetchDataAndUpdateViews() {
        val jsonUrl =
            "https://raw.githubusercontent.com/marek-guran/linux-server-info/main/update.json"
        FetchJsonDataTask().execute(jsonUrl)
    }

    private fun schedulePeriodicUpdates() {
        // Implement your periodic update logic here if needed
    }

    private inner class FetchJsonDataTask : AsyncTask<String, Void, JSONObject>() {
        override fun doInBackground(vararg params: String?): JSONObject? {
            val urlString = params[0]
            try {
                val url = URL(urlString)
                val connection = url.openConnection() as HttpURLConnection

                try {
                    val inputStream: InputStream = connection.inputStream
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val stringBuilder = StringBuilder()
                    var line: String?

                    while (reader.readLine().also { line = it } != null) {
                        stringBuilder.append(line)
                    }

                    return JSONObject(stringBuilder.toString())
                } finally {
                    connection.disconnect()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return null
        }

        override fun onPostExecute(jsonObject: JSONObject?) {
            super.onPostExecute(jsonObject)
            jsonObject?.let {
                val title = it.optString("Title", "")
                val description = it.optString("Description", "")

                // Access UI elements only if the fragment's view is available
                view?.let { fragmentView ->
                    val titleTextView = fragmentView.findViewById<TextView>(R.id.title)
                    val descriptionTextView = fragmentView.findViewById<TextView>(R.id.description)
                    val updateButton = fragmentView.findViewById<Button>(R.id.update_btn)

                    titleTextView.text = title
                    descriptionTextView.text = description

                    updateButton.setOnClickListener {
                        val webIntent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://github.com/marek-guran/linux-server-info/tree/main")
                        )
                        startActivity(webIntent)
                    }
                }
            }
        }
    }
}
