package com.airstream.typhoon.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airstream.typhoon.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FilterSheetFragment : BottomSheetDialogFragment() {

    private val searchViewModel: SearchViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_filter_sheet, container, false)

        val filtersList: RecyclerView = root.findViewById(R.id.listview_filters)
        filtersList.layoutManager = LinearLayoutManager(requireActivity())



        return root
    }
}