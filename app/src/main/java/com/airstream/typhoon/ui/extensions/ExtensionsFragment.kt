package com.airstream.typhoon.ui.extensions

import android.app.ActionBar
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airstream.typhoon.R
import com.airstream.typhoon.adapter.ExtensionAdapter

class ExtensionsFragment : Fragment() {

    private val extensionsViewModel: ExtensionsViewModel by viewModels()
    private var extensionsAdapter = ExtensionAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_extensions, container, false)

        val toolbar = (requireActivity() as AppCompatActivity).supportActionBar
        val navSpinner: Spinner = requireActivity().findViewById(R.id.nav_spinner)

        toolbar?.setDisplayShowTitleEnabled(true)
        navSpinner?.visibility = View.GONE

        val extensionsList: RecyclerView = root.findViewById(R.id.listview_extension)
        extensionsList.layoutManager = LinearLayoutManager(requireActivity())
        extensionsList.setHasFixedSize(true)
        extensionsList.adapter = extensionsAdapter

        extensionsViewModel.installedExtensions.observe(viewLifecycleOwner, Observer {
            val installableExtensions = extensionsViewModel.installableExtensions
            Log.d(TAG, "onCreateView: $it")
            extensionsAdapter.clear()
            extensionsAdapter.addAll(it + installableExtensions)
        })

        return root
    }
    
    companion object {
        private const val TAG = "ExtensionsFragment"
    }
}