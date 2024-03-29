package com.airstream.typhoon.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airstream.typhoon.R
import com.airstream.typhoon.adapter.SeriesAdapter
import com.uvnode.typhoon.extensions.model.Series

class HistoryFragment : Fragment() {

    private val historyViewModel: HistoryViewModel by viewModels()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        val toolbar = (requireActivity() as AppCompatActivity).supportActionBar
        val navSpinner: Spinner = requireActivity().findViewById(R.id.nav_spinner)

        toolbar?.setDisplayShowTitleEnabled(true)
        navSpinner?.visibility = View.GONE

        return root
    }
}