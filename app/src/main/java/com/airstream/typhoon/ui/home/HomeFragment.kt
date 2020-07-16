package com.airstream.typhoon.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toolbar
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airstream.typhoon.R
import com.airstream.typhoon.adapter.SeriesAdapter
import com.uvnode.typhoon.extensions.model.Series
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.coroutines.runBlocking

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var navSpinner: Spinner
    private var toolbar: ActionBar? = null
//    private var seriesAdapter: SeriesAdapter = SeriesAdapter(requireActivity(), emptyList())
    private var rankingsAdapter: ArrayAdapter<CharSequence>? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
                ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        toolbar = (requireActivity() as AppCompatActivity).supportActionBar
        navSpinner = requireActivity().findViewById(R.id.nav_spinner)

        navSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
//                homeViewModel.
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        val gridView: RecyclerView = root.findViewById(R.id.gridview_home)
        gridView.layoutManager = GridLayoutManager(activity, R.integer.gridview_series_columns)
        gridView.setHasFixedSize(true)
//        gridView.adapter = seriesAdapter
        homeViewModel.currentSource.observe(viewLifecycleOwner, Observer {
            updateUi()
        })

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        rankingsAdapter = ArrayAdapter(requireActivity().toolbar.context, android.R.layout.simple_spinner_item)
        navSpinner.adapter = rankingsAdapter
    }

    private fun updateUi() {
        if (homeViewModel.isCurrentSourceRankable()) {
            navSpinner.visibility = View.VISIBLE
            toolbar?.setDisplayShowTitleEnabled(false)

//            rankingsAdapter?.clear()
//            homeViewModel.getRankingsNames().let { rankingsAdapter?.addAll(it) }

        } else {
            navSpinner.visibility = View.GONE
            toolbar?.setDisplayShowTitleEnabled(true)
        }
    }

    companion object {
        private const val TAG = "HomeFragment"
    }
}