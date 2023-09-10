package com.marekguran.serverinfo

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class TemperatureMonitoringService : Service() {

    private val handler = Handler(Looper.getMainLooper())
    private val notificationChannelId = "CPU_TEMP_CHANNEL"
    private lateinit var apiAddressManager: ApiAddressManager

    private val jsonUrl: String
        get() = apiAddressManager.getApiAddress()

    override fun onCreate() {
        super.onCreate()
        // Initialize apiAddressManager here
        apiAddressManager = ApiAddressManager(this.applicationContext)
    }

    private var notificationSent = false // Flag to track if notification has been sent

    @RequiresApi(api = Build.VERSION_CODES.O)
    fun createNotificationChannel() {
        val name = "CPU Temperature Warning"
        val descriptionText = "High CPU temperature warning"
        val importance = NotificationManager.IMPORTANCE_HIGH

        val channel = NotificationChannel(notificationChannelId, name, importance).apply {
            description = descriptionText
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        // Start monitoring CPU temperature
        startMonitoringTemperature()

        return START_STICKY
    }

    private fun startMonitoringTemperature() {
        // Fetch JSON data initially
        fetchDataAndUpdateTemperature()

        // Schedule periodic data refresh every 5 seconds
        handler.postDelayed(object : Runnable {
            override fun run() {
                fetchDataAndUpdateTemperature()
                handler.postDelayed(this, 5000) // 5000 milliseconds = 5 seconds
            }
        }, 5000) // Initial delay also set to 5 seconds
    }

    private fun fetchDataAndUpdateTemperature() {
        FetchJsonDataTask().execute(jsonUrl)
    }

    private inner class FetchJsonDataTask : AsyncTask<String, Void, Float>() {
        override fun doInBackground(vararg params: String?): Float? {
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

                    val jsonObject = JSONObject(stringBuilder.toString())
                    val cpu = jsonObject.getJSONObject("cpu")
                    val temperatureStr = cpu.getString("temperature")
                    return temperatureStr.toFloatOrNull()
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

        override fun onPostExecute(temperature: Float?) {
            super.onPostExecute(temperature)
            temperature?.let {
                // Check if the temperature exceeds 80 degrees
                if (temperature >= 80 && !notificationSent) {
                    sendHighPriorityNotification("High CPU Temperature!", temperature)
                    notificationSent = true // Set the flag to indicate that a notification has been sent
                } else if (temperature < 80) {
                    notificationSent = false // Reset the flag when the temperature is below 80
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun sendHighPriorityNotification(title: String, temperature: Float) {
        val message = "$temperature℃"
        val builder = NotificationCompat.Builder(this, notificationChannelId)
            .setSmallIcon(R.drawable.app_icon)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notification = builder.build()

        startForeground(1, notification) // Start the service in the foreground with the notification

        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(1, notification)
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
