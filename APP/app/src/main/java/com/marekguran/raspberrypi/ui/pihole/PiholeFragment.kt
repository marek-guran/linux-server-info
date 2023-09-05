package com.marekguran.raspberrypi.ui.pihole

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.marekguran.raspberrypi.databinding.FragmentPiholeBinding

class PiholeFragment : Fragment() {

    private var _binding: FragmentPiholeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPiholeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val totalqueriesTextView: TextView = binding!!.totalQueries
        val totalblockedTextView: TextView = binding!!.totalBlocked
        val totalqueriespercentTextView: TextView = binding!!.totalQueriesPercent
        val totaladslistTextView: TextView = binding!!.totalAdsList
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}