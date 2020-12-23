package com.airstream.typhoon.ui.search

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.airstream.typhoon.utils.Injector
import com.uvnode.typhoon.extensions.model.Filter
import com.uvnode.typhoon.extensions.model.PaginatedList

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val sourceRepository = Injector.getSourceRepository(application)
    private val seriesRepository = Injector.getSeriesRepository(application)

    val currentSource = sourceRepository.currentSource

    private fun getFiltersList(): MutableList<Filter<Any>> {
        val filters: MutableList<Filter<Any>> = sourceRepository.getFilters(currentSource.value?.let {
            sourceRepository.getSourceById(it)
        })?.toMutableList() ?: mutableListOf()

        filters.add(0, Filter.Query("") as Filter<Any>)

        return filters
    }

    private val filters: List<Filter<Any>> = getFiltersList()
    var q: String
        get() = filters[0].state as String
        set(value) {
            _seriesList.value = null
            filters[0].state = value
        }
    var page = 1
    var lastItem: Int? = null

    private val _seriesList: MutableLiveData<PaginatedList?> by lazy {
        MutableLiveData<PaginatedList?>()
    }

    val seriesList: LiveData<PaginatedList?> = _seriesList

    suspend fun search(append: Boolean) {
        if (!append && seriesList.value == null) {
            val result = seriesRepository.search(currentSource.value?.let {
                sourceRepository.getSourceById(it)
            }, filters, page)

            _seriesList.value = result
            Log.d(TAG, "getSeriesList: Updated from network")
        } else if (append) {
            val result = seriesRepository.search(currentSource.value?.let {
                sourceRepository.getSourceById(it)
            }, filters, page)

            _seriesList.value = PaginatedList(_seriesList.value!!.list + result!!.list, result.isHasNext)

            Log.d(TAG, "getSeriesList: Appended from network")
        }

        Log.d(TAG, "search: ${seriesList.value?.list}")
        Log.d(TAG, "search: ${seriesList.value!!.isHasNext} $page")
    }
    
    companion object {
        private const val TAG = "SearchViewModel"
    }
}