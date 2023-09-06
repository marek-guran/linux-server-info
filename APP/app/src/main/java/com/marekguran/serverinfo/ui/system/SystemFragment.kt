package com.marekguran.serverinfo.ui.system

import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.marekguran.serverinfo.R
import com.marekguran.serverinfo.databinding.FragmentSystemBinding
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.TimeUnit

class SystemFragment : Fragment() {

    private var _binding: FragmentSystemBinding? = null
    private val binding get() = _binding!!

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSystemBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        handler.removeCallbacksAndMessages(null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Fetch JSON data initially
        fetchDataAndUpdateViews()

        // Schedule periodic updates every second
        schedulePeriodicUpdates()
    }

    private fun fetchDataAndUpdateViews() {
        val jsonUrl = "http://10.0.1.1:9000/system_info.json" // Adjust the URL as needed
        FetchJsonDataTask().execute(jsonUrl)
    }

    private fun formatUptime(uptimeInSeconds: Double): String {
        val days = TimeUnit.SECONDS.toDays(uptimeInSeconds.toLong())
        val hours = TimeUnit.SECONDS.toHours(uptimeInSeconds.toLong()) % 24
        val minutes = TimeUnit.SECONDS.toMinutes(uptimeInSeconds.toLong()) % 60
        return String.format("%02d:%02d:%02d", days, hours, minutes)
    }

    private fun schedulePeriodicUpdates() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                fetchDataAndUpdateViews()
                handler.postDelayed(this, 1000) // Update every 1 second
            }
        }, 1000) // Initial delay also set to 1 second
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
                val os = jsonObject.getJSONObject("os")
                val distribution = os.getString("distribution")
                val kernel = os.getString("kernel_version")
                val cpuName = jsonObject.getJSONObject("cpu").getString("hardware")
                val cpuArchitecture = jsonObject.getJSONObject("cpu").getString("architecture")
                val cpuArchitectureType = jsonObject.getJSONObject("cpu").getString("architecture_type")
                val cpuType = jsonObject.getJSONObject("cpu").getString("type")
                val cpuCores = jsonObject.getJSONObject("cpu").getString("cores")
                val uptimeInSeconds = os.getString("uptime")
                val networkDevices = jsonObject.getJSONObject("network")

                if (!TextUtils.isEmpty(distribution)) {
                    binding.Distribution.text = "Distribution: $distribution"
                }
                if (!TextUtils.isEmpty(kernel)) {
                    binding.Kernel.text = "Kernel: $kernel"
                }
                if (!TextUtils.isEmpty(uptimeInSeconds)) {
                    val uptimeSplit = uptimeInSeconds.split(".")
                    val uptimeInSecondsInt = uptimeSplit[0].toLong()
                    val uptimeFraction = uptimeSplit[1].substring(0, 2).toLong() // Extract seconds as well

                    val timestamp = uptimeInSecondsInt * 1000 + uptimeFraction // Convert to milliseconds
                    val currentTimeMillis = System.currentTimeMillis()
                    val uptimeMillis = currentTimeMillis - timestamp

                    val days = TimeUnit.MILLISECONDS.toDays(uptimeMillis)
                    val hours = TimeUnit.MILLISECONDS.toHours(uptimeMillis) % 24
                    val minutes = TimeUnit.MILLISECONDS.toMinutes(uptimeMillis) % 60
                    val seconds = TimeUnit.MILLISECONDS.toSeconds(uptimeMillis) % 60

                    val formattedUptime = String.format("%02d:%02d:%02d:%02d", days, hours, minutes, seconds)
                    binding.Uptime.text = "Uptime: $formattedUptime"
                }
                if (!TextUtils.isEmpty(cpuName)) {
                    binding.CpuName.text = "CPU: $cpuName"
                }
                if (!TextUtils.isEmpty(cpuArchitecture)) {
                    binding.CpuArchitecture.text = "CPU Architecture: $cpuArchitectureType - $cpuArchitecture"
                }
                if (!TextUtils.isEmpty(cpuType)) {
                    binding.CpuType.text = "CPU Type: $cpuType"
                }
                if (!TextUtils.isEmpty(cpuCores)) {
                    val coresJson = jsonObject.getJSONObject("cpu").getJSONObject("cores")
                    var coreCount = 0
                    val keys = coresJson.keys()

                    while (keys.hasNext()) {
                        val key = keys.next()
                        if (key.startsWith("core_")) {
                            coreCount++
                        }
                    }

                    val cpuCores = "CPU Cores: $coreCount"
                    binding.CpuCores.text = cpuCores
                }
                if (!TextUtils.isEmpty(networkDevices.toString())) {
                    val networkJson = jsonObject.getJSONObject("network")
                    var networkDeviceCount = 0
                    val keys = networkJson.keys()

                    while (keys.hasNext()) {
                        keys.next() // Move to the next key
                        networkDeviceCount++
                    }

                    val networkDevices = "Network Devices: $networkDeviceCount"
                    binding.NetworkDevices.text = networkDevices
                }
                val distributionImageResource = when {
                    distribution?.toLowerCase()?.contains("ubuntu") == true -> R.drawable.sys_ubuntu
                    distribution?.toLowerCase()?.contains("debian") == true -> R.drawable.sys_debian
                    distribution?.toLowerCase()?.contains("raspbian") == true -> R.drawable.sys_raspbian
                    else -> R.drawable.sys_default
                }
                binding.distributionImage.setImageResource(distributionImageResource)

                // Set CPU image based on CPU name
                val cpuImageResource = when {
                    cpuName?.toLowerCase()?.contains("amd") == true -> R.drawable.amd
                    cpuName?.toLowerCase()?.contains("intel") == true -> R.drawable.intel
                    cpuName?.toLowerCase()?.contains("bcm") == true -> R.drawable.broadcom
                    else -> R.drawable.sys_cpu
                }
                binding.cpuImage.setImageResource(cpuImageResource)
            }
        }
    }
}
