package com.airstream.typhoon.ui.header

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.airstream.typhoon.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.app_bar_main.*

class HeaderFragment : Fragment() {

    private val headerViewModel: HeaderViewModel by viewModels()
    private lateinit var sourcesSpinner: Spinner
    private var adapter: ArrayAdapter<CharSequence>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_header, container, false)
        val sourceImage: ImageView = root.findViewById(R.id.image_sources)
        val infoButton: ImageButton = root.findViewById(R.id.button_info)
        sourcesSpinner = root.findViewById(R.id.spinner_sources)

        sourcesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                Log.d(TAG, "onItemSelected: $p2")
                headerViewModel.setCurrentSource(p2)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        headerViewModel.sources.observe(viewLifecycleOwner, Observer {
            Log.d(TAG, "onCreateView: $it")
            adapter?.addAll(headerViewModel.getSourceNames(it))
            sourcesSpinner.setSelection(headerViewModel.getCurrentSourceIndex())
        })

        headerViewModel.currentSource.observe(viewLifecycleOwner, Observer {
            val currentSource = headerViewModel.sources.value?.get(headerViewModel.getCurrentSourceIndex())
            val image = currentSource?.source?.image

            Picasso.get().load(image).centerCrop().fit().placeholder(R.drawable.source_image_placeholder).into(sourceImage)
        })
        
        infoButton.setOnClickListener {

        }

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        adapter = ArrayAdapter(requireActivity().toolbar.context, android.R.layout.simple_spinner_item)
        adapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sourcesSpinner.adapter = adapter
    }

    companion object {
        private const val TAG = "HeaderFragment"
    }
}