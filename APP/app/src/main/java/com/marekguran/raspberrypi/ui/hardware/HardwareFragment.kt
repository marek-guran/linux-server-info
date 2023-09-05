package com.marekguran.raspberrypi.ui.hardware

import android.R
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
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import com.marekguran.raspberrypi.databinding.FragmentHardwareBinding
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.ref.WeakReference
import java.net.HttpURLConnection
import java.net.URL
import androidx.core.content.ContextCompat

class HardwareFragment : Fragment() {

    private var _binding: FragmentHardwareBinding? = null
    private val binding get() = _binding!!
    private val jsonUrl = "http://10.0.1.1:9000/system_info.json"

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
        val distributionTextView: TextView = binding.distribution
        val ramUsageTextView: TextView = binding.ramUsage
        val ramFreeTextView: TextView = binding.ramFree
        val ramTotalTextView: TextView = binding.ramTotal
        val storageContainer: LinearLayout = binding.storageContainer


        // Fetch JSON data initially
        fetchDataAndUpdateViews(cpuUsageTextView, cpuTempTextView, cpuClockTextView, distributionTextView, ramFreeTextView, ramTotalTextView, ramUsageTextView, storageContainer)

        // Schedule periodic data refresh every 5 seconds
        handler.postDelayed(object : Runnable {
            override fun run() {
                fetchDataAndUpdateViews(cpuUsageTextView, cpuTempTextView, cpuClockTextView, distributionTextView, ramUsageTextView, ramFreeTextView, ramTotalTextView, storageContainer)
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
        distributionTextView: TextView,
        ramUsageTextView: TextView,
        ramFreeTextView: TextView,
        ramTotalTextView: TextView,
        storageContainer: LinearLayout
    ) {
        FetchJsonDataTask(cpuUsageTextView, cpuTempTextView, cpuClockTextView, distributionTextView, ramTotalTextView, ramFreeTextView, ramUsageTextView, storageContainer).execute(jsonUrl)
    }

    private class FetchJsonDataTask(
        private val cpuUsageTextView: TextView,
        private val cpuTempTextView: TextView,
        private val cpuClockTextView: TextView,
        private val distributionTextView: TextView,
        private val ramTotalTextView: TextView,
        private val ramFreeTextView: TextView,
        private val ramUsedTextView: TextView,
        private val storageContainer: LinearLayout
    ) : AsyncTask<String, Void, JSONObject>() {

        private val weakCpuUsageTextView: WeakReference<TextView> = WeakReference(cpuUsageTextView)
        private val weakCpuTempTextView: WeakReference<TextView> = WeakReference(cpuTempTextView)
        private val weakCpuClockTextView: WeakReference<TextView> = WeakReference(cpuClockTextView)
        private val weakDistributionTextView: WeakReference<TextView> = WeakReference(distributionTextView)
        private val weakRamFreeTextView: WeakReference<TextView> = WeakReference(ramFreeTextView)
        private val weakRamTotalTextView: WeakReference<TextView> = WeakReference(ramTotalTextView)
        private val weakRamUsageTextView: WeakReference<TextView> = WeakReference(ramUsedTextView)

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
            jsonObject?.let { updateTextViews(it)
                createStorageViews(it)}
            Log.d("JSON Data", jsonObject.toString())
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

                val storageNameTextView = TextView(weakCpuUsageTextView.get()?.context)
                storageNameTextView.text = name
                storageNameTextView.setTextColor(Color.parseColor("#f2f2fb"))

                val progressBar = ProgressBar(
                    weakCpuUsageTextView.get()?.context,
                    null,
                    R.attr.progressBarStyleHorizontal
                )
                progressBar.progress = usagePercent.replace("%", "").toFloat().toInt()

                val progressBarTextView = TextView(weakCpuUsageTextView.get()?.context)
                progressBarTextView.text = "Used $usedStorage out of $totalStorage"
                progressBarTextView.setTextColor(Color.parseColor("#f2f2fb"))

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
                val cpuTemp = cpuInfo.getString("temperature")
                val cpuClock = cpuInfo.getString("speed")

                val os = jsonObject.getJSONObject("os")
                val distribution = os.getString("distribution")

                val ram = jsonObject.getJSONObject("ram")
                val ramUsage = ram.getString("used")
                val ramFree = ram.getString("free")
                val ramTotal = ram.getString("total")

                weakCpuUsageTextView.get()?.text = "Usage: $cpuUsage"
                weakCpuTempTextView.get()?.text = "Temperature: $cpuTemp℃"
                weakCpuClockTextView.get()?.text = "Clock Speed: $cpuClock"
                weakDistributionTextView.get()?.text = "$distribution"
                weakRamUsageTextView.get()?.text = "Used: $ramUsage"
                weakRamFreeTextView.get()?.text = "Free: $ramFree"
                weakRamTotalTextView.get()?.text = "Total: $ramTotal"
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}