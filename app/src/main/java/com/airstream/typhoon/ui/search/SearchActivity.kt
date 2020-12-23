package com.airstream.typhoon.ui.search

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airstream.typhoon.R
import com.airstream.typhoon.adapter.RecyclerListener
import com.airstream.typhoon.adapter.SeriesAdapter
import com.airstream.typhoon.ui.home.HomeFragment
import com.airstream.typhoon.ui.series.SeriesActivity
import com.airstream.typhoon.utils.ItemClickSupport
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.uvnode.typhoon.extensions.model.Series
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchActivity: AppCompatActivity() {

    private val searchViewModel: SearchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val seriesAdapter = SeriesAdapter()

        seriesAdapter.onBottomReachedListener = RecyclerListener.OnBottomReachedListener {
            Log.d(TAG, "reached bottom")
            if(searchViewModel.seriesList.value!!.isHasNext && (searchViewModel.lastItem != it)) {
                searchViewModel.page++
                CoroutineScope(Dispatchers.Main).launch {
                    searchViewModel.search(true)
                }
                searchViewModel.lastItem = it
                Toast.makeText(this, R.string.loading_series, Toast.LENGTH_SHORT).show()
            }
        }

        val gridView: RecyclerView = findViewById(R.id.gridview_search)
        gridView.layoutManager = GridLayoutManager(this, resources.getInteger(R.integer.gridview_series_columns))
        gridView.setHasFixedSize(true)
        gridView.adapter = seriesAdapter

        ItemClickSupport.addTo(gridView).setOnItemClickListener { recyclerView, position, v ->
            val series = seriesAdapter.getItem(position)

            val intent = Intent(this, SeriesActivity::class.java).apply {
                putExtra("series", series)
                putExtra("source", searchViewModel.currentSource.value)
            }
            startActivity(intent)
        }

        val filterFloatingActionButton: FloatingActionButton = findViewById(R.id.fab_filter)
        filterFloatingActionButton.setOnClickListener {
//            val filterSheetFragment = FilterSheetFragment()
//            filterSheetFragment.show(supportFragmentManager, "Filter")
        }

        searchViewModel.seriesList.observe(this, Observer {
            it?.let {
                gridView.adapter = seriesAdapter

                seriesAdapter.clear()
                seriesAdapter.addAll(it.list!! as List<Series>)

                Log.d(TAG, "onCreateView: get ${it.list}")
            }
        })

        if (savedInstanceState == null) {
            searchViewModel.q = intent.extras?.getString("query").toString()
        }

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        updateSearch()
    }

    fun updateSearch() {
        CoroutineScope(Dispatchers.Main).launch {
            searchViewModel.search(false)
        }

        supportActionBar!!.title =
            resources.getString(R.string.search_results, searchViewModel.q)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search, menu)

        val searchView: SearchView = menu?.findItem(R.id.action_search)?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrBlank()) {
                    searchViewModel.q = query
                    searchViewModel.page = 1
                    searchView.setQuery("", false)
                    searchView.isIconified = true

                    updateSearch()
                    return true
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            else -> {
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }
    
    companion object {
        private const val TAG = "SearchActivity"
    }
}