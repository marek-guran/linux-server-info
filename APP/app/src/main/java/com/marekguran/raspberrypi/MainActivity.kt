package com.marekguran.raspberrypi

import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.marekguran.raspberrypi.databinding.ActivityMainBinding
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_hw, R.id.navigation_system, R.id.navigation_monitoring, R.id.navigation_settings
            )
        )
        navView.setupWithNavController(navController)

        // Fetch JSON data initially
        fetchDataAndUpdateIcon(navView)

        // Schedule periodic data refresh every 5 seconds
        handler.postDelayed(object : Runnable {
            override fun run() {
                fetchDataAndUpdateIcon(navView)
                handler.postDelayed(this, 5000) // 5000 milliseconds = 5 seconds
            }
        }, 5000) // Initial delay also set to 5 seconds
    }

    private fun fetchDataAndUpdateIcon(navView: BottomNavigationView) {
        val jsonUrl = "http://10.0.1.1:9000/system_info.json" // Adjust the URL as needed
        FetchJsonDataTask(navView).execute(jsonUrl)
    }

    private class FetchJsonDataTask(private val navView: BottomNavigationView) :
        AsyncTask<String, Void, JSONObject>() {

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
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return null
        }

        override fun onPostExecute(jsonObject: JSONObject?) {
            super.onPostExecute(jsonObject)
            jsonObject?.let {
                // Assuming you have parsed the distribution data
                val os = jsonObject.getJSONObject("os")
                val distribution = os.getString("distribution").toLowerCase()

                // Determine the drawable resource based on the distribution
                val drawableResId = when {
                    distribution.contains("ubuntu") -> R.drawable.dist_ubuntu
                    distribution.contains("debian") -> R.drawable.dist_debian
                    distribution.contains("raspbian") -> R.drawable.dist_raspbian
                    else -> R.drawable.dist_default // Default drawable if no match is found
                }

                // Find the menu item in your navigation view
                val menuItem = navView.menu.findItem(R.id.navigation_system)

                // Update the icon for the menu item
                menuItem.setIcon(drawableResId)
            }
        }

    }
}
