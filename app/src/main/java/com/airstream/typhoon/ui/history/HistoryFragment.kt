package com.airstream.typhoon.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airstream.typhoon.R
import com.airstream.typhoon.adapter.SeriesAdapter
import com.uvnode.typhoon.extensions.model.Series

class HistoryFragment : Fragment() {

    private lateinit var historyViewModel: HistoryViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        historyViewModel =
                ViewModelProvider(this).get(HistoryViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        return root
    }
}