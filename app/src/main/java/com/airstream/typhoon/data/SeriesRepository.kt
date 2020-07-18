package com.airstream.typhoon.data

import com.uvnode.typhoon.extensions.model.Ranking
import com.uvnode.typhoon.extensions.model.Series
import com.uvnode.typhoon.extensions.source.MetaSource
import kotlinx.coroutines.*

class SeriesRepository private constructor(private val sourceManager: SourceManager) {

    suspend fun getRankings(sourceById: MetaSource?): List<Ranking>? = sourceManager.getRankings(sourceById)

    suspend fun getSeriesList(sourceById: MetaSource?): List<Series>? = sourceManager.getSeriesList(sourceById)

    suspend fun getSeriesListWithRanking(sourceById: MetaSource?, ranking: Ranking): List<Series>? = sourceManager.getSeriesList(sourceById, ranking)

    companion object {
        @Volatile private var instance: SeriesRepository? = null;

        fun getInstance(sourceManager: SourceManager) =
            instance ?: synchronized(this) {
                instance ?: SeriesRepository(sourceManager).also { instance = it }
            }
    }
}