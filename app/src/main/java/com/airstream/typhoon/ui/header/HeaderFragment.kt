package com.airstream.typhoon.ui.header

import android.media.Image
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.airstream.typhoon.R
import kotlinx.android.synthetic.main.app_bar_main.*

class HeaderFragment : Fragment() {

    private lateinit var headerViewModel: HeaderViewModel
    private lateinit var sourcesSpinner: Spinner
    private var adapter: ArrayAdapter<CharSequence>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        headerViewModel =
            ViewModelProvider(this).get(HeaderViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_header, container, false)
        val infoButton: ImageButton = root.findViewById(R.id.button_info)
        sourcesSpinner = root.findViewById(R.id.spinner_sources)

        sourcesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                headerViewModel.setCurrentSource(p2)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        headerViewModel.getSources().observe(viewLifecycleOwner, Observer {
            adapter?.clear()
            adapter?.addAll(headerViewModel.getSourceNames()!!)
            sourcesSpinner.setSelection(headerViewModel.getCurrentSourceIndex())
        })

        infoButton.setOnClickListener {

        }

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        adapter = ArrayAdapter(requireActivity().toolbar.context, android.R.layout.simple_spinner_item)
        sourcesSpinner.adapter = adapter
    }

    companion object {
        private const val TAG = "HeaderFragment"
    }
}