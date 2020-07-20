package com.airstream.typhoon.ui.series

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import com.airstream.typhoon.utils.Injector
import com.uvnode.typhoon.extensions.model.Listing
import com.uvnode.typhoon.extensions.model.Series
import kotlinx.coroutines.launch

class SeriesViewModel(application: Application) : AndroidViewModel(application) {

    private val sourceRepository = Injector.getSourceRepository(application)
    private val seriesRepository = Injector.getSeriesRepository(application)
    private val preferences = PreferenceManager.getDefaultSharedPreferences(application)

    var sourceId: String? = null

    val series: MutableLiveData<Series> by lazy {
        MutableLiveData<Series>()
    }

    private val _episodes: MutableLiveData<List<Listing>> by lazy {
        MutableLiveData<List<Listing>>()
    }

    private val _sortOrder: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(preferences.getBoolean(LIST_ORDERING_KEY, true))
    }

    val episodes: LiveData<List<Listing>> = _episodes
    var currentListing = MutableLiveData<Int>()
    val sortOrder: LiveData<Boolean> = _sortOrder

    fun getListings() {
        if (episodes.value == null) {
            val source = sourceRepository.getSourceById(sourceId)
            viewModelScope.launch {
                _episodes.value = seriesRepository.getListings(source, series.value)

                Log.d(TAG, "getListings: ${episodes.value}")
            }
        }
    }

    fun toggleOrder() {
        val newSortOrder = !(sortOrder.value!!)

        val preferencesEditor = preferences.edit()
        preferencesEditor.putBoolean(LIST_ORDERING_KEY, newSortOrder)
        preferencesEditor.apply()

        _sortOrder.value = newSortOrder
    }

    companion object {
        private const val TAG = "SeriesViewModel"

        private const val LIST_ORDERING_KEY = "listOrdering"
    }
}