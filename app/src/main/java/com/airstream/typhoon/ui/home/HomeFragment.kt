package com.airstream.typhoon.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airstream.typhoon.R
import com.airstream.typhoon.ui.series.SeriesActivity
import com.airstream.typhoon.adapter.SeriesAdapter
import com.airstream.typhoon.utils.ItemClickSupport
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.coroutines.*

class HomeFragment : Fragment() {

    private val homeViewModel: HomeViewModel by viewModels()
    private var navSpinner: Spinner? = null
    private var toolbar: ActionBar? = null
    private var seriesAdapter: SeriesAdapter = SeriesAdapter()
    private var rankingsAdapter: ArrayAdapter<CharSequence>? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        toolbar = (requireActivity() as AppCompatActivity).supportActionBar
        navSpinner = requireActivity().findViewById(R.id.nav_spinner)

        navSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                homeViewModel.switchRanking(p2)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        val gridView: RecyclerView = root.findViewById(R.id.gridview_home)
        gridView.layoutManager = GridLayoutManager(requireActivity(), resources.getInteger(R.integer.gridview_series_columns))
        gridView.setHasFixedSize(true)
        gridView.adapter = seriesAdapter

        ItemClickSupport.addTo(gridView).setOnItemClickListener { recyclerView, position, v ->
            val series = seriesAdapter.getItem(position)

            val intent = Intent(requireActivity(), SeriesActivity::class.java).apply {
                putExtra("series", series)
                putExtra("source", homeViewModel.currentSource.value)
            }
            requireActivity().startActivity(intent)
        }

        homeViewModel.currentSource.observe(viewLifecycleOwner, Observer {
            var currentRanking = 0
            if (savedInstanceState != null) {
                currentRanking = savedInstanceState.getInt("currentRanking", 0)
                if (savedInstanceState.getString("currentSource") != it) {
                    homeViewModel.switchSource()
                }
            } else {
                homeViewModel.switchSource()
            }

            updateUi(it, currentRanking)

            Log.d(TAG, "onCreateView: currentSource $it")
        })

        homeViewModel.seriesList.observe(viewLifecycleOwner, Observer {
            it?.let {
                seriesAdapter.clear()
                seriesAdapter.addAll(it)

                gridView.adapter = seriesAdapter

                Log.d(TAG, "onCreateView: get $it")
            }
        })

        return root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        homeViewModel.currentSource.value?.let { outState.putString("currentSource", it) }
        navSpinner?.selectedItemPosition?.let { outState.putInt("currentRanking", it) }
    }

    private fun updateUi(currentSource: String, pos: Int) {
        if (homeViewModel.isSourceRankable(currentSource)) {
            CoroutineScope(Dispatchers.Default).launch {
                homeViewModel.getRankingsNames().let {
                    withContext(Dispatchers.Main) {
                        navSpinner?.visibility = View.VISIBLE
                        toolbar?.setDisplayShowTitleEnabled(false)

                        rankingsAdapter = ArrayAdapter(requireActivity().toolbar.context, android.R.layout.simple_spinner_item)
                        rankingsAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        navSpinner?.adapter = rankingsAdapter

                        rankingsAdapter?.clear()
                        rankingsAdapter?.addAll(it)

                        navSpinner?.setSelection(pos)
                    }
                }
            }
        } else {
            navSpinner?.visibility = View.GONE
            toolbar?.setDisplayShowTitleEnabled(true)
            CoroutineScope(Dispatchers.Main).launch {
                homeViewModel.getSeriesList()
            }
        }
    }

    companion object {
        private const val TAG = "HomeFragment"
    }
}