package com.airstream.typhoon.ui.extensions

import android.app.ActionBar
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airstream.typhoon.R

class ExtensionsFragment : Fragment() {

    private lateinit var extensionsViewModel: ExtensionsViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        extensionsViewModel =
                ViewModelProvider(this).get(ExtensionsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_extensions, container, false)

        return root
    }
}