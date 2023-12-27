package com.marekguran.serverinfo.ui.hardware

import android.annotation.SuppressLint
import android.content.Context
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
import com.marekguran.serverinfo.R as R2
import com.marekguran.serverinfo.databinding.FragmentHardwareBinding
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.ref.WeakReference
import java.net.HttpURLConnection
import java.net.URL

class HardwareFragment : Fragment() {

    private var _binding: FragmentHardwareBinding? = null
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
        _binding = FragmentHardwareBinding.inflate(inflater, container, false)
        val root: View = binding.root

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
                handler.postDelayed(this, 5000)
            }
        }, 5000)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
            ramUsageProgress,
            requireContext()
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
        private val ramUsageProgress: ProgressBar,
        private val context: Context
    ) : AsyncTask<String, Void, JSONObject>() {

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
            networkContainer.removeAllViews()

            val networkObject = jsonObject.getJSONObject("network")

            val networkNames = networkObject.keys()
            while (networkNames.hasNext()) {
                val name = networkNames.next()
                val networkData = networkObject.getJSONObject(name)

                val isUp = networkData.getBoolean("is_up")
                val speed = networkData.getString("speed")

                val networkNameTextView = TextView(context)
                networkNameTextView.text = name
                networkNameTextView.setTextColor(Color.parseColor("#f2f2fb"))
                networkNameTextView.textSize = 16F

                val networkSpeedTextView = TextView(context)
                val runningLabel = context.getString(R2.string.running)
                val speedLabel = context.getString(R2.string.speed)

                networkSpeedTextView.text = "$runningLabel $isUp\n$speedLabel $speed"
                networkSpeedTextView.setTextColor(Color.parseColor("#f2f2fb"))
                networkSpeedTextView.setPadding(0, 0, 0, 10)
                networkSpeedTextView.textSize = 16F

                networkContainer.addView(networkNameTextView)
                networkContainer.addView(networkSpeedTextView)
            }
        }

        private fun createStorageViews(jsonObject: JSONObject) {
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

                val mounted = context.getString(R2.string.mounted_at)
                val fs = context.getString(R2.string.fs)

                val storageNameTextView = TextView(context)
                storageNameTextView.text = "$name \n$mounted $mountedStorage\n" +
                        "$fs $fstypeStorage"
                storageNameTextView.setTextColor(Color.parseColor("#f2f2fb"))
                storageNameTextView.textSize = 16F
                storageNameTextView.setPadding(0, 0, 0, 5)

                val progressBar = ProgressBar(
                    context,
                    null,
                    android.R.attr.progressBarStyleHorizontal
                )
                progressBar.progress = usagePercent.replace("%", "").toFloat().toInt()
                progressBar.progressDrawable = ContextCompat.getDrawable(context, R2.drawable.custom_progress_bar)

                val progressBarHeightInPixels = 40
                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    progressBarHeightInPixels
                )

                progressBar.layoutParams = layoutParams

                val storage1 = context.getString(R2.string.used_storage1)
                val storage2 = context.getString(R2.string.used_storage2)

                val progressBarTextView = TextView(context)
                progressBarTextView.text = "$storage1 $usedStorage $storage2 $totalStorage"
                progressBarTextView.setTextColor(Color.parseColor("#f2f2fb"))
                progressBarTextView.textSize = 16F
                progressBarTextView.setPadding(0, 5, 0, 10)

                storageContainer.addView(storageNameTextView)
                storageContainer.addView(progressBar)
                storageContainer.addView(progressBarTextView)
            }
        }

        private fun updateTextViews(jsonObject: JSONObject) {
            try {
                val cpuInfo = jsonObject.getJSONObject("cpu")
                val cpuUsage = cpuInfo.getString("usage")
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

                cpuUsageTextView.text = cpuUsage
                cpuUsageProgressBar.progress = cpuUsageProgressValue
                cpuTempProgressBar.progress = cpuTempProgressValue
                cpuTempTextView.text = "$cpuTemp℃"
                val currentCpuClockText = cpuClockTextView.text.toString()
                cpuClockTextView.text = currentCpuClockText.split(":")[0] + ": $cpuClock"
                val currentRamFreeText = ramFreeTextView.text.toString()
                ramFreeTextView.text = currentRamFreeText.split(":")[0] + ": $ramFree"
                val currentRamUsageText = ramUsedTextView.text.toString()
                ramUsedTextView.text = currentRamUsageText.split(":")[0] + ": $ramUsage"
                val currentRamTotalText = ramTotalTextView.text.toString()
                ramTotalTextView.text = currentRamTotalText.split(":")[0] + ": $ramTotal"
                ramUsagePercent.text = ramPercent
                ramUsageProgress.progress = ramProgressValue
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
