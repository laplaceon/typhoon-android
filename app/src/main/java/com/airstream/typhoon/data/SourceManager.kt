package com.airstream.typhoon.data

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.airstream.typhoon.utils.Injector
import com.airstream.typhoon.utils.SyncCookieHandler
import com.uvnode.typhoon.extensions.api.ApiCallbacks
import com.uvnode.typhoon.extensions.api.ApiError
import com.uvnode.typhoon.extensions.api.ApiResponse
import com.uvnode.typhoon.extensions.executor.JSEClient
import com.uvnode.typhoon.extensions.model.*
import com.uvnode.typhoon.extensions.source.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import okhttp3.Cache
import okhttp3.OkHttpClient
import java.util.ArrayList
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class SourceManager(private val ctx: Context) {

    private val networkHelper = Injector.getNetworkHelper(ctx)

    private val extensionManager = Injector.getExtensionManager(ctx)

    private val extensions = extensionManager.getInstalledExtensions()

    val sourcesMap: MutableMap<String, Int> = mutableMapOf()

    fun getSources(): LiveData<List<MetaSource>> = Transformations.map(extensions) {
        val sources = mutableListOf<MetaSource>()
        sourcesMap.clear()

        for (extension in extensions.value!!) {
            extension.extension?.sources?.forEach {
                if (it is UsesContext) {
                    it.setContext(ctx)
                }
                if (it is Configurable) {
                    it.setSharedPreferences(ctx.getSharedPreferences("", Context.MODE_PRIVATE))
                }
                if (it is HttpSource) {
                    it.okClient = networkHelper.okClient
                    it.jseClient = networkHelper.jseClient
                }
                sources.add(it)
                sourcesMap[it.source.id] = sources.size - 1
            }
        }

        Log.d(TAG, "getSources: ")

        sources
    }

    fun getFilters(source: MetaSource?): List<Filter<*>>? = (source as Filterable).filters

    suspend fun search(source: MetaSource?, filters: List<Filter<Any>>, page: Int): PaginatedList? =
        suspendCoroutine {
            val callback = object: ApiCallbacks {
                override fun onFailure(error: ApiError) {
                    it.resume(null)
                }

                override fun onResponse(response: ApiResponse?) {
                    val results: PaginatedList = response?.payload as PaginatedList
                    it.resume(results)
                }
            }

            (source as Filterable).search(callback, filters, page)
        }

    suspend fun getSeries(source: MetaSource?, series: Series): Series? =
        suspendCoroutine {
            val callback = object: ApiCallbacks {
                override fun onFailure(error: ApiError) {
                    it.resume(null)
                }

                override fun onResponse(response: ApiResponse?) {
                    if (response?.result == ApiResponse.SOME) {
                        it.resume(response.payload as Series)
                    } else {
                        it.resume(series)
                    }

                }
            }

            source?.getSeries(callback, series)
        }

    suspend fun getSeriesList(source: MetaSource?): List<Series>? =
        suspendCoroutine {
            val callback = object: ApiCallbacks {
                override fun onFailure(error: ApiError) {
                    it.resume(null)
                }

                override fun onResponse(response: ApiResponse?) {
                    val series: List<Series> = response?.payload as List<Series>
                    it.resume(series)
                }
            }

            source?.getSeriesList(callback)
        }

    suspend fun getSeriesList(source: MetaSource?, ranking: Ranking): List<Series>? =
        suspendCoroutine {
            val callback = object: ApiCallbacks {
                override fun onFailure(error: ApiError) {
                    it.resume(null)
                }

                override fun onResponse(response: ApiResponse?) {
                    val series: List<Series> = response?.payload as List<Series>
                    it.resume(series)
                }
            }

            (source as Rankable).getSeriesList(callback, ranking)
        }

    suspend fun getRankings(source: MetaSource?): List<Ranking>? =
        suspendCoroutine {
            val callback = object: ApiCallbacks {
                override fun onFailure(error: ApiError) {
                    it.resume(null)
                }

                override fun onResponse(response: ApiResponse?) {
                    val rankings: List<Ranking> = response?.payload as List<Ranking>
                    it.resume(rankings)
                }
            }

            (source as Rankable).getRankings(callback)
        }

    suspend fun getEpisodesList(source: MetaSource?, series: Series): List<Episode>? =
        suspendCoroutine {
            val callback = object: ApiCallbacks {
                override fun onFailure(error: ApiError) {
                    it.resume(null)
                }

                override fun onResponse(response: ApiResponse?) {
                    val episodes: List<Episode> = response?.payload as List<Episode>

                    it.resume(episodes)
                }
            }

            source?.getEpisodesList(callback, series)
        }

    suspend fun getEpisodesList(source: MetaSource?, series: Series, listing: Listing): List<Episode>? =
        suspendCoroutine {
            val callback = object: ApiCallbacks {
                override fun onFailure(error: ApiError) {
                    it.resume(null)
                }

                override fun onResponse(response: ApiResponse?) {
                    if (response?.result == ApiResponse.SOME) {
                        val episodes: List<Episode> = response.payload as List<Episode>

                        it.resume(episodes)
                    } else {
                        it.resume(listing.episodes)
                    }
                }
            }

            (source as HasListings).getEpisodesList(callback, series, listing)
        }

    suspend fun getListings(source: MetaSource?, series: Series): List<Listing>? =
        suspendCoroutine {
            val hasListings = source is HasListings

            val callback = object : ApiCallbacks {
                override fun onFailure(error: ApiError) {
                    it.resume(null)
                }

                override fun onResponse(response: ApiResponse?) {
                    if (hasListings) {
                        val listings: List<Listing> = response?.payload as List<Listing>

                        it.resume(listings)
                    } else {
                        val episodes: List<Episode> = response?.payload as List<Episode>

                        val listings: List<Listing> = listOf(Listing(episodes = episodes, id = "", name = ""))
                        listings[0].episodes = episodes as ArrayList<Episode>?
                        listings[0].id = ""
                        listings[0].name = ""

                        it.resume(listings)
                    }
                }
            }

            if (hasListings) {
                (source as HasListings).getListings(callback, series)
            } else {
                source?.getEpisodesList(callback, series)
            }
        }

    suspend fun getVideoUris(source: MetaSource?, series: Series, episode: Episode): WatchableResponse? =
        suspendCoroutine {
            val callback = object : ApiCallbacks {
                override fun onFailure(error: ApiError) {
                    it.resume(null)
                }

                override fun onResponse(response: ApiResponse?) {
                    val videoResponse: WatchableResponse = response?.payload as WatchableResponse

                    it.resume(videoResponse)
                }
            }

            source?.getVideoUris(callback, series, episode)
        }

    suspend fun getVideoUris(source: MetaSource?, series: Series, episode: Episode, mirror: Mirror): VideoResponse? =
        suspendCoroutine {
            val callback = object : ApiCallbacks {
                override fun onFailure(error: ApiError) {
                    it.resume(null)
                }

                override fun onResponse(response: ApiResponse?) {
                    val videoResponse: VideoResponse = response?.payload as VideoResponse

                    it.resume(videoResponse)
                }
            }

            (source as HasMirrors).getVideoUris(callback, series, episode, mirror)
        }


    companion object {
        private const val TAG = "SourceManager"
    }

}