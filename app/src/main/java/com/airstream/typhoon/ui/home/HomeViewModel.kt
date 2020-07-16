package com.airstream.typhoon.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.airstream.typhoon.utils.Injector
import com.uvnode.typhoon.extensions.model.Ranking
import com.uvnode.typhoon.extensions.source.Rankable
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val sourceRepository = Injector.getSourceRepository(application)
    private val seriesRepository = Injector.getSeriesRepository(application)

    private var rankings: List<Ranking>? = null

//    val currentRanking =
    val currentSource = sourceRepository.currentSource

    fun isCurrentSourceRankable() = !currentSource.value.isNullOrBlank() && (sourceRepository.getSourceById(currentSource.value!!) is Rankable)

    private fun getRankings(): List<Ranking> {
        if (rankings == null) {
            runBlocking { rankings = seriesRepository.getRankings(sourceRepository.getSourceById(currentSource.value!!)) }
        }

        return rankings!!
    }

    fun resetRankings() {
        rankings = null
    }

    fun getRankingsNames() = getRankings().map { it.name }



}