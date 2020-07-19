package com.airstream.typhoon.ui.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.airstream.typhoon.utils.Injector
import com.uvnode.typhoon.extensions.model.Ranking
import com.uvnode.typhoon.extensions.model.Series
import com.uvnode.typhoon.extensions.source.Rankable
import kotlinx.coroutines.*
import okhttp3.internal.wait
import kotlin.coroutines.CoroutineContext

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val sourceRepository = Injector.getSourceRepository(application)
    private val seriesRepository = Injector.getSeriesRepository(application)

    var rankings: List<Ranking>? = null

    private val _seriesList: MutableLiveData<List<Series>?> by lazy {
        MutableLiveData<List<Series>?>()
    }

    private var currentRanking = -1

    val seriesList: LiveData<List<Series>?> = _seriesList

    val currentSource = sourceRepository.currentSource

    fun isSourceRankable(currentSource: String) = !currentSource.isBlank() && (sourceRepository.getSourceById(currentSource) is Rankable)

    private suspend fun getRankings(): List<Ranking> {
        if (rankings == null) {
            rankings = seriesRepository.getRankings(sourceRepository.getSourceById(currentSource.value!!))
            Log.d(TAG, "getRankings Called")
        }
        Log.d(TAG, "getRankings Cache Called")

        return rankings!!
    }

    suspend fun getRankingsNames() = getRankings().map { it.name }

    private suspend fun getSeriesList(rankingPos: Int) {
        if (_seriesList.value == null) {
            val ranking = rankings!![rankingPos]
            _seriesList.value = seriesRepository.getSeriesListWithRanking(sourceRepository.getSourceById(currentSource.value!!), ranking)
            Log.d(TAG, "getSeriesList: Updated from network")
        }
    }

    suspend fun getSeriesList() {
        if (_seriesList.value == null) {
            _seriesList.value = seriesRepository.getSeriesList(sourceRepository.getSourceById(currentSource.value))
            Log.d(TAG, "getSeriesList: Updated from data source")
        }
    }

    fun switchRanking(p2: Int) {
        Log.d(TAG, "switchRanking: $p2, $currentRanking")
        if (currentRanking != p2) {
            viewModelScope.launch {
                _seriesList.value = null
                getSeriesList(p2)
            }
        }
        currentRanking = p2
    }

    fun switchSource() {
        _seriesList.value = null
        rankings = null
        currentRanking = -1
    }


    companion object {
        private const val TAG = "HomeViewModel"
    }

}