package com.airstream.typhoon.ui.player

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.airstream.typhoon.BuildConfig
import com.airstream.typhoon.analytics.AdsManager
import com.airstream.typhoon.utils.Injector
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.upstream.DataSource
import com.google.common.base.Stopwatch
import com.uvnode.typhoon.extensions.model.*

class PlayerViewModel(application: Application) : AndroidViewModel(application) {

    private val sourceRepository = Injector.getSourceRepository(application)
    private val seriesRepository = Injector.getSeriesRepository(application)

    val networkHelper = Injector.getNetworkHelper(application)

    lateinit var player: SimpleExoPlayer

    lateinit var series: Series
    lateinit var episode: Episode

    var uris: List<Video> = listOf()
    var mirrors: List<Mirror> = listOf()
    var backupUri = ""

    var currentTime = 0L
    var quality = -1

    var resumedFromHistory = false
    var resumeFromMirror = 0L
    var playerHidden = false
    val stopwatch: Stopwatch = Stopwatch.createUnstarted()

    var okDataSourceFactory: DataSource.Factory? = null
    var defaultDataSourceFactory: DataSource.Factory? = null

    val adsManager = AdsManager()

    fun resume() {
        val resumeOffset = 500
        currentTime = if (currentTime > resumeOffset) {
            // Rewind a little
            currentTime - resumeOffset
        } else {
            0
        }
        player.seekTo(currentTime)

    }

    suspend fun getVideoUris(): WatchableResponse? {
        val source = sourceRepository.getSourceById(series.source)

        return seriesRepository.getVideoUris(source, series, episode)
    }

    suspend fun getVideoUrisFromMirror(mirror: Mirror): VideoResponse? {
        val source = sourceRepository.getSourceById(series.source)

        return seriesRepository.getVideoUris(source, series, episode, mirror)
    }

    companion object {
        private const val TAG = "PlayerViewModel"
    }

}