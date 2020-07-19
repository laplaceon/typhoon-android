package com.airstream.typhoon.ui.library

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
import com.airstream.typhoon.R

class LibraryFragment : Fragment() {

    private val libraryViewModel: LibraryViewModel by viewModels()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_library, container, false)

        val toolbar = (requireActivity() as AppCompatActivity).supportActionBar
        val navSpinner: Spinner = requireActivity().findViewById(R.id.nav_spinner)

        toolbar?.setDisplayShowTitleEnabled(true)
        navSpinner?.visibility = View.GONE

        val textView: TextView = root.findViewById(R.id.text_library)
        libraryViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}