@file:Suppress("DEPRECATION", "NAME_SHADOWING")

package com.marekguran.serverinfo.ui.system

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.marekguran.serverinfo.ApiAddressManager
import com.marekguran.serverinfo.R
import com.marekguran.serverinfo.databinding.FragmentSystemBinding
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale
import com.marekguran.serverinfo.R as R2
import java.util.concurrent.TimeUnit

class SystemFragment : Fragment() {

    private var _binding: FragmentSystemBinding? = null
    private val binding get() = _binding!!

    private fun getApi(): String {
        val apiAddressManager = ApiAddressManager(requireContext())
        return apiAddressManager.getApiAddress()
    }

    private val jsonUrl: String
        get() = getApi()

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSystemBinding.inflate(inflater, container, false)
        return binding.root
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
        FetchJsonDataTask().execute(jsonUrl)
    }

    private fun schedulePeriodicUpdates() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                fetchDataAndUpdateViews()
                handler.postDelayed(this, 1000) // Update every 1 second
            }
        }, 500) // Initial delay also set to 500 mili-seconds
    }

    private inner class FetchJsonDataTask : AsyncTask<String, Void, JSONObject>() {
        @Deprecated("Deprecated in Java")
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

        @SuppressLint("SetTextI18n")
        @Deprecated("Deprecated in Java")
        override fun onPostExecute(jsonObject: JSONObject?) {
            val localBinding = _binding
            if (localBinding == null || jsonObject == null) {
                super.onPostExecute(jsonObject)
                return
            }

            with(localBinding) {
                jsonObject?.let { json ->
                    // Parse OS information
                    val os = json.getJSONObject("os")
                    val distribution = os.getString("distribution")
                    val kernel = os.getString("kernel_version")
                    val uptimeInSeconds = os.getString("uptime")

                    // Parse CPU information
                    val cpu = json.getJSONObject("cpu")
                    val coresJson = cpu.getJSONObject("cores")
                    var coreCount = 0
                    val keys = coresJson.keys()
                    val cpuName = cpu.getString("hardware")
                    val cpuArchitecture = cpu.getString("architecture")
                    val cpuArchitectureType = cpu.getString("architecture_type")
                    val cpuType = cpu.getString("type")
                    val cpuCores = cpu.getString("cores")

                    // Parse network information
                    val networkJson = json.getJSONObject("network")
                    var networkDeviceCount = 0
                    val networkkeys = networkJson.keys()

                    while (networkkeys.hasNext()) {
                        networkkeys.next() // Move to the next key
                        networkDeviceCount++
                    }

                    val networkDevicesText = getString(R2.string.network_devices)
                    val networkDevicesDisplay = "$networkDevicesText $networkDeviceCount"
                    NetworkDevices.text = networkDevicesDisplay

                    // Update UI components with the parsed data
                    if (!TextUtils.isEmpty(distribution)) {
                        Distribution.text = "${getString(R2.string.distribution)} $distribution"
                    }
                    if (!TextUtils.isEmpty(kernel)) {
                        Kernel.text = "${getString(R2.string.kernel)} $kernel"
                    }
                    if (!TextUtils.isEmpty(uptimeInSeconds)) {
                        val uptimeSplit = uptimeInSeconds.split(".")
                        val uptimeInSecondsInt = uptimeSplit[0].toLong()
                        val uptimeFraction = if (uptimeSplit.size > 1) uptimeSplit[1].substring(0, 2).toLong() else 0L

                        val timestamp = uptimeInSecondsInt * 1000 + uptimeFraction // Convert to milliseconds
                        val currentTimeMillis = System.currentTimeMillis()
                        val uptimeMillis = currentTimeMillis - timestamp

                        val days = TimeUnit.MILLISECONDS.toDays(uptimeMillis)
                        val hours = TimeUnit.MILLISECONDS.toHours(uptimeMillis) % 24
                        val minutes = TimeUnit.MILLISECONDS.toMinutes(uptimeMillis) % 60
                        val seconds = TimeUnit.MILLISECONDS.toSeconds(uptimeMillis) % 60

                        val formattedUptime = String.format("%02d:%02d:%02d:%02d", days, hours, minutes, seconds)
                        Uptime.text = "${getString(R2.string.uptime)} $formattedUptime"
                    }
                    if (!TextUtils.isEmpty(cpuName)) {
                        CpuName.text = "${getString(R2.string.cpu_sys)} $cpuName"
                    }
                    if (!TextUtils.isEmpty(cpuArchitecture)) {
                        CpuArchitecture.text = "${getString(R2.string.architecture)} $cpuArchitectureType - $cpuArchitecture"
                    }
                    if (!TextUtils.isEmpty(cpuType)) {
                        CpuType.text = "${getString(R2.string.type)} $cpuType"
                    }
                    while (keys.hasNext()) {
                        val key = keys.next()
                        if (key.startsWith("core_")) {
                            coreCount++
                        }
                    }

                    CpuCores.text = "${getString(R2.string.cores)} $coreCount"

                    val distributionImageResource = when {
                        distribution.toLowerCase(Locale.ROOT)
                            .contains("ubuntu") -> R.drawable.sys_ubuntu

                        distribution.toLowerCase(Locale.ROOT)
                            .contains("debian") -> R.drawable.sys_debian

                        distribution.toLowerCase(Locale.ROOT)
                            .contains("raspbian") -> R.drawable.sys_raspbian

                        distribution.toLowerCase(Locale.ROOT)
                            .contains("raspberry") -> R.drawable.sys_raspbian

                        else -> R.drawable.sys_default
                    }
                    binding.distributionImage.setImageResource(distributionImageResource)

                    // Set CPU image based on CPU name
                    val cpuImageResource = when {
                        cpuName.toLowerCase(Locale.ROOT).contains("amd") -> R.drawable.amd
                        cpuName.toLowerCase(Locale.ROOT).contains("intel") -> R.drawable.intel
                        cpuName.toLowerCase(Locale.ROOT).contains("bcm") -> R.drawable.broadcom
                        else -> R.drawable.sys_cpu
                    }
                    binding.cpuImage.setImageResource(cpuImageResource)
                }
            }

        }
    }
}
