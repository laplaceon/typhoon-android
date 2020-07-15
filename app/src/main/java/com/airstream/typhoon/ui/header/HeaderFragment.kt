package com.airstream.typhoon.ui.header

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.airstream.typhoon.R

class HeaderFragment : Fragment() {

    private lateinit var headerViewModel: HeaderViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        headerViewModel =
            ViewModelProvider(this).get(HeaderViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_header, container, false)
        val sourcesSpinner: Spinner = root.findViewById(R.id.spinner_sources)

        return root
    }

}