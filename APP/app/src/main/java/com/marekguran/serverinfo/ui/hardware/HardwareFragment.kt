package com.marekguran.serverinfo.ui.hardware

import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.marekguran.serverinfo.ApiAddressManager
import com.marekguran.serverinfo.databinding.FragmentHardwareBinding
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.ref.WeakReference
import java.net.HttpURLConnection
import java.net.URL
import android.R as R1
import com.marekguran.serverinfo.R as R2

class HardwareFragment : Fragment() {

    private var _binding: FragmentHardwareBinding? = null
    private val binding get() = _binding!!

    private fun getApi(): String {
        val apiAddressManager = ApiAddressManager(requireContext())
        return apiAddressManager.getApiAddress()
    }

    private val jsonUrl: String
        get() = getApi()

    // Handler to periodically fetch data
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHardwareBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize TextViews
        val cpuUsageTextView: TextView = binding.cpuUsage
        val cpuTempTextView: TextView = binding.cpuTemp
        val cpuClockTextView: TextView = binding.cpuSpeed
        val ramUsageTextView: TextView = binding.ramUsage
        val ramFreeTextView: TextView = binding.ramFree
        val ramTotalTextView: TextView = binding.ramTotal
        val storageContainer: LinearLayout = binding.storageContainer
        val networkContainer: LinearLayout = binding.networkContainer
        val cpuUsageProgressBar: ProgressBar = binding.cpuUsageProgress
        val cpuTempProgressBar: ProgressBar = binding.cpuTemperatureProgress
        val ramUsagePercent: TextView = binding.ramUsagePercent
        val ramUsageProgress: ProgressBar = binding.ramUsageProgress

        // Fetch JSON data initially
        fetchDataAndUpdateViews(
            cpuUsageTextView,
            cpuTempTextView,
            cpuClockTextView,
            ramFreeTextView,
            ramTotalTextView,
            ramUsageTextView,
            storageContainer,
            networkContainer,
            cpuUsageProgressBar,
            cpuTempProgressBar,
            ramUsagePercent,
            ramUsageProgress
        )

        // Schedule periodic data refresh every 5 seconds
        handler.postDelayed(object : Runnable {
            override fun run() {
                fetchDataAndUpdateViews(
                    cpuUsageTextView,
                    cpuTempTextView,
                    cpuClockTextView,
                    ramUsageTextView,
                    ramFreeTextView,
                    ramTotalTextView,
                    storageContainer,
                    networkContainer,
                    cpuUsageProgressBar,
                    cpuTempProgressBar,
                    ramUsagePercent,
                    ramUsageProgress
                )
                handler.postDelayed(this, 5000) // 5000 milliseconds = 5 seconds
            }
        }, 5000) // Initial delay also set to 5 seconds

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        // Remove any pending callbacks when the fragment is destroyed
        handler.removeCallbacksAndMessages(null)
    }

    private fun fetchDataAndUpdateViews(
        cpuUsageTextView: TextView,
        cpuTempTextView: TextView,
        cpuClockTextView: TextView,
        ramUsageTextView: TextView,
        ramFreeTextView: TextView,
        ramTotalTextView: TextView,
        storageContainer: LinearLayout,
        networkContainer: LinearLayout,
        cpuUsageProgressBar: ProgressBar,
        cpuTempProgressBar: ProgressBar,
        ramUsagePercent: TextView,
        ramUsageProgress: ProgressBar
    ) {
        FetchJsonDataTask(
            cpuUsageTextView,
            cpuTempTextView,
            cpuClockTextView,
            ramTotalTextView,
            ramFreeTextView,
            ramUsageTextView,
            storageContainer,
            networkContainer,
            cpuUsageProgressBar,
            cpuTempProgressBar,
            ramUsagePercent,
            ramUsageProgress
        ).execute(jsonUrl)
    }

    private class FetchJsonDataTask(
        private val cpuUsageTextView: TextView,
        private val cpuTempTextView: TextView,
        private val cpuClockTextView: TextView,
        private val ramTotalTextView: TextView,
        private val ramFreeTextView: TextView,
        private val ramUsedTextView: TextView,
        private val storageContainer: LinearLayout,
        private val networkContainer: LinearLayout,
        private val cpuUsageProgressBar: ProgressBar,
        private val cpuTempProgressBar: ProgressBar,
        private val ramUsagePercent: TextView,
        private val ramUsageProgress: ProgressBar
    ) : AsyncTask<String, Void, JSONObject>() {

        private val weakCpuUsageTextView: WeakReference<TextView> = WeakReference(cpuUsageTextView)
        private val weakCpuTempTextView: WeakReference<TextView> = WeakReference(cpuTempTextView)
        private val weakCpuClockTextView: WeakReference<TextView> = WeakReference(cpuClockTextView)
        private val weakRamUsageTextView: WeakReference<TextView> = WeakReference(ramUsedTextView)
        private val weakRamFreeTextView: WeakReference<TextView> = WeakReference(ramFreeTextView)
        private val weakRamTotalTextView: WeakReference<TextView> = WeakReference(ramTotalTextView)
        private val weakRamUsagePercentTextView: WeakReference<TextView> = WeakReference(ramUsagePercent)

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
                updateTextViews(it)
                createStorageViews(it)
                createNetworkViews(it)
            }
            Log.d("JSON Data", jsonObject.toString())
        }

        private fun createNetworkViews(jsonObject: JSONObject) {
            // Clear existing views
            networkContainer.removeAllViews()

            val networkObject = jsonObject.getJSONObject("network")

            val networkNames = networkObject.keys()
            while (networkNames.hasNext()) {
                val name = networkNames.next()
                val networkData = networkObject.getJSONObject(name)

                val isUp = networkData.getBoolean("is_up")
                val speed = networkData.getString("speed")

                val networkNameTextView = TextView(weakCpuUsageTextView.get()?.context)
                networkNameTextView.text = name
                networkNameTextView.setTextColor(Color.parseColor("#f2f2fb"))
                networkNameTextView.setTextSize(16F)

                val networkSpeedTextView = TextView(weakCpuUsageTextView.get()?.context)
                networkSpeedTextView.text = "Running: $isUp \nSpeed: $speed"
                networkSpeedTextView.setTextColor(Color.parseColor("#f2f2fb"))
                networkSpeedTextView.setPadding(0, 0, 0, 10)
                networkSpeedTextView.setTextSize(16F)

                // Add the views to the network container
                networkContainer.addView(networkNameTextView)
                networkContainer.addView(networkSpeedTextView)
            }
        }

        private fun createStorageViews(jsonObject: JSONObject) {
            // Clear existing views
            storageContainer.removeAllViews()

            val storageArray = jsonObject.getJSONArray("storage")

            for (i in 0 until storageArray.length()) {
                val storageObject = storageArray.getJSONObject(i)
                val name = storageObject.getString("name")
                val usagePercent = storageObject.getString("usage_percent")
                val usedStorage = storageObject.getString("used")
                val totalStorage = storageObject.getString("size")
                val mountedStorage = storageObject.getString("mountpoint")
                val fstypeStorage = storageObject.getString("fstype")

                val storageNameTextView = TextView(weakCpuUsageTextView.get()?.context)
                storageNameTextView.text = "$name \nMounted at: $mountedStorage\n" +
                        "File System: $fstypeStorage"
                storageNameTextView.setTextColor(Color.parseColor("#f2f2fb"))
                storageNameTextView.setTextSize(16F)
                storageNameTextView.setPadding(0, 0, 0, 5)

                val progressBar = ProgressBar(
                    weakCpuUsageTextView.get()?.context,
                    null,
                    R1.attr.progressBarStyleHorizontal
                )
                progressBar.progress = usagePercent.replace("%", "").toFloat().toInt()
                // Retrieve the Drawable from the resource ID using ContextCompat
                progressBar.progressDrawable = ContextCompat.getDrawable(weakCpuUsageTextView?.get()?.context!!, R2.drawable.custom_progress_bar)

                val progressBarHeightInPixels = 40

                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    progressBarHeightInPixels
                )

                progressBar.layoutParams = layoutParams

                val progressBarTextView = TextView(weakCpuUsageTextView.get()?.context)
                progressBarTextView.text = "Used $usedStorage out of $totalStorage"
                progressBarTextView.setTextColor(Color.parseColor("#f2f2fb"))
                progressBarTextView.setTextSize(16F)
                progressBarTextView.setPadding(0, 5, 0, 10)

                // Add the views to the storage container
                storageContainer.addView(storageNameTextView)
                storageContainer.addView(progressBar)
                storageContainer.addView(progressBarTextView)
            }
        }

        private fun updateTextViews(jsonObject: JSONObject) {
            try {
                val cpuInfo = jsonObject.getJSONObject("cpu")
                val cpuUsage = cpuInfo.getString("usage")
                // Extract the numeric part (excluding %) and convert it to an integer
                val cpuUsageProgressValue = cpuUsage.split(".")[0].toIntOrNull() ?: 0
                val cpuTemp = cpuInfo.getString("temperature")
                val cpuTempProgressValue = cpuTemp.split(".")[0].toIntOrNull() ?: 0
                val cpuClock = cpuInfo.getString("speed")

                val ram = jsonObject.getJSONObject("ram")
                val ramUsage = ram.getString("used")
                val ramFree = ram.getString("free")
                val ramTotal = ram.getString("total")
                val ramPercent = ram.getString("usage_percent")
                val ramProgressValue = ramPercent.split("%")[0].toIntOrNull() ?: 0

                weakCpuUsageTextView.get()?.text = cpuUsage
                cpuUsageProgressBar.progress = cpuUsageProgressValue
                cpuTempProgressBar.progress = cpuTempProgressValue
                weakCpuTempTextView.get()?.text = "$cpuTemp℃"
                weakCpuClockTextView.get()?.text = "Clock Speed: $cpuClock"
                weakRamUsageTextView.get()?.text = "Used: $ramUsage"
                weakRamFreeTextView.get()?.text = "Free: $ramFree"
                weakRamTotalTextView.get()?.text = "Total: $ramTotal"
                weakRamUsagePercentTextView.get()?.text = ramPercent
                ramUsageProgress.progress = ramProgressValue
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}


