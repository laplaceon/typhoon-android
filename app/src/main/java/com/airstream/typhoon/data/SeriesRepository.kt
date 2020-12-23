package com.airstream.typhoon.data

import com.uvnode.typhoon.extensions.model.*
import com.uvnode.typhoon.extensions.source.MetaSource
import kotlinx.coroutines.*

class SeriesRepository private constructor(private val sourceManager: SourceManager) {

    suspend fun search(sourceById: MetaSource?, filters: List<Filter<Any>>, page: Int): PaginatedList? = sourceManager.search(sourceById, filters, page)

    suspend fun getRankings(sourceById: MetaSource?): List<Ranking>? = sourceManager.getRankings(sourceById)

    suspend fun getSeriesList(sourceById: MetaSource?): List<Series>? = sourceManager.getSeriesList(sourceById)

    suspend fun getSeriesListWithRanking(sourceById: MetaSource?, ranking: Ranking): List<Series>? = sourceManager.getSeriesList(sourceById, ranking)

    suspend fun getSeries(sourceById: MetaSource?, series: Series) = sourceManager.getSeries(sourceById, series)

    suspend fun getListings(sourceById: MetaSource?, series: Series?) = sourceManager.getListings(sourceById, series)

    suspend fun getEpisodesList(sourceById: MetaSource?, series: Series) = sourceManager.getEpisodesList(sourceById, series)

    suspend fun getEpisodesListWithListing(sourceById: MetaSource?, series: Series, listing: Listing) = sourceManager.getEpisodesList(sourceById, series, listing)

    companion object {
        @Volatile private var instance: SeriesRepository? = null;

        fun getInstance(sourceManager: SourceManager) =
            instance ?: synchronized(this) {
                instance ?: SeriesRepository(sourceManager).also { instance = it }
            }
    }
}