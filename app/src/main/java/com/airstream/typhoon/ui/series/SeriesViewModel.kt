package com.airstream.typhoon.ui.series

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import com.airstream.typhoon.utils.Injector
import com.uvnode.typhoon.extensions.model.Episode
import com.uvnode.typhoon.extensions.model.Listing
import com.uvnode.typhoon.extensions.model.Series
import com.uvnode.typhoon.extensions.source.HasListings
import kotlinx.coroutines.launch
import java.util.ArrayList

class SeriesViewModel(application: Application) : AndroidViewModel(application) {

    private val sourceRepository = Injector.getSourceRepository(application)
    private val seriesRepository = Injector.getSeriesRepository(application)
    private val preferences = PreferenceManager.getDefaultSharedPreferences(application)

    var sourceId: String? = null

    val series: MutableLiveData<Series> by lazy {
        MutableLiveData<Series>()
    }

    private val _listings: MutableLiveData<List<Listing>> by lazy {
        MutableLiveData<List<Listing>>()
    }

    private val _episodes: MutableLiveData<List<Episode>> by lazy {
        MutableLiveData<List<Episode>>()
    }

    private val _sortOrder: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(preferences.getBoolean(LIST_ORDERING_KEY, true))
    }

    val listings: LiveData<List<Listing>> = _listings
    val episodes: LiveData<List<Episode>> = _episodes
    val sortOrder: LiveData<Boolean> = _sortOrder

    var currentListing: Int = -1
    private var seriesInfoRetrieved = false

    private fun getEpisodesList() {
        if (episodes.value == null) {
            val source = sourceRepository.getSourceById(sourceId!!)
            if (source is HasListings) {
                listings.value?.get(currentListing)?.let {
                    Log.d(TAG, "getEpisodesList: $it")
                    viewModelScope.launch {
                        _episodes.value = seriesRepository.getEpisodesListWithListing(source, series.value!!, it) as ArrayList<Episode>?
                    }
                }
            } else {
                viewModelScope.launch {
                    _episodes.value = seriesRepository.getEpisodesList(source, series.value!!) as ArrayList<Episode>?
                }
            }
        }
    }

    fun getListings() {
        if (listings.value == null) {
            val source = sourceRepository.getSourceById(sourceId!!)
            viewModelScope.launch {
                _listings.value = seriesRepository.getListings(source, series.value)
                Log.d(TAG, "getListings: ${series.value}")
            }
        }
    }

    fun switchListing(listing: Int) {
        if (currentListing != listing) {
            currentListing = listing
            Log.d(TAG, "switchListing: $listing")
            _episodes.value = null
            getEpisodesList()
        }
    }

    fun toggleOrder() {
        val newSortOrder = !(sortOrder.value!!)

        val preferencesEditor = preferences.edit()
        preferencesEditor.putBoolean(LIST_ORDERING_KEY, newSortOrder)
        preferencesEditor.apply()

        _sortOrder.value = newSortOrder
    }

    fun getSourceInfo() {
        if (!seriesInfoRetrieved) {
            val source = sourceRepository.getSourceById(sourceId!!)
            viewModelScope.launch {
                series.value = seriesRepository.getSeries(source, series.value!!)
                seriesInfoRetrieved = true
            }
        }
    }

    companion object {
        private const val TAG = "SeriesViewModel"

        private const val LIST_ORDERING_KEY = "listOrdering"
    }
}