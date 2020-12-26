package com.airstream.typhoon.ui.player

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.airstream.typhoon.R
import com.airstream.typhoon.utils.Injector
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSourceFactory
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.dash.DashChunkSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.uvnode.typhoon.extensions.model.MirrorResponse
import com.uvnode.typhoon.extensions.model.Video
import com.uvnode.typhoon.extensions.model.VideoResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileFilter
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern


class PlayerActivity : AppCompatActivity() {

    private val playerViewModel: PlayerViewModel by viewModels()

    private lateinit var playerView: PlayerView

    private lateinit var resolutionSwitcher: TextView
    private lateinit var mirrorSelector: TextView
    private lateinit var viewerSwitcher: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        playerViewModel.player = SimpleExoPlayer.Builder(this).build()
        playerViewModel.player.addListener(getEventListener())

        playerView = findViewById(R.id.video_view)
        playerView.player = playerViewModel.player

        playerView.setControllerVisibilityListener {
            if (it == PlayerControlView.VISIBLE) {
                showSourceControls()
            } else {
                hideSourceControls()
                setFullscreen()
            }
        }

        resolutionSwitcher = findViewById(R.id.switcher_resolution)
        mirrorSelector = findViewById(R.id.selector_mirror)
        viewerSwitcher = findViewById(R.id.switcher_view)
        progressBar = findViewById(R.id.progress_bar)

        resolutionSwitcher.setOnClickListener {
            if(playerViewModel.uris.size > 1) {
                showResolutionSelectionDialog();
            } else {
                Toast.makeText(this, R.string.player_video_options_none, Toast.LENGTH_SHORT).show();
            }
        }

        mirrorSelector.setOnClickListener {
            if(playerViewModel.mirrors.isNotEmpty()) {
                showMirrorSelectionDialog();
            }
        }

        viewerSwitcher.setOnClickListener {
            switchToWebPlayer();
        }

        // Set series and episode
        val extras = intent.extras!!
        playerViewModel.episode = extras.getParcelable("episode")!!
        playerViewModel.series = extras.getParcelable("series")!!

        Log.d(TAG, "onCreate: ${playerViewModel.episode.id}")

        hideSourceControls()
        mirrorSelector.visibility = TextView.GONE
        setFullscreen()

        val shared = getPreferences(Context.MODE_PRIVATE)
        playerViewModel.quality = shared.getInt("quality", -1)

        if (playerViewModel.quality == -1) {
            val numCores: Int = getNumCores()
            if (numCores == 1) {
                playerViewModel.quality = 1
            } else if (numCores in 2..3) {
                playerViewModel.quality = 2
            } else {
                playerViewModel.quality = 3
            }
        }

        playerViewModel.okDataSourceFactory = OkHttpDataSourceFactory(playerViewModel.networkHelper.okClient, playerViewModel.networkHelper.jseClient.userAgent)
        playerViewModel.defaultDataSourceFactory = DefaultDataSourceFactory(this, playerViewModel.networkHelper.jseClient.userAgent)

        getUris()
    }

    private fun getUris() {
        CoroutineScope(Dispatchers.Default).launch {
            playerViewModel.getVideoUris()?.let {
                if (it is MirrorResponse) {
                    playerViewModel.mirrors = it.mirrors
                    Log.d(TAG, "getUris: ${it.mirrors}")

                    withContext(Dispatchers.Main) {
                        progressBar.visibility = ProgressBar.VISIBLE;
                        mirrorSelector.visibility = TextView.VISIBLE;
                        showMirrorSelectionDialog();
                    }
                } else if (it is VideoResponse) {
                    handleVideoResponse(it)
                }
            }
        }
    }

    private fun initializePlayer() {
        Log.d(TAG, "initializePlayer: ${playerViewModel.quality} | + ${playerViewModel.uris}")
        playerViewModel.quality = 0
        if (playerViewModel.uris.isNotEmpty()) {
            val video: Video = playerViewModel.uris[playerViewModel.quality]
            resolutionSwitcher.text = playerViewModel.uris[playerViewModel.quality].name
            playUrl(video)
        }
    }

    private suspend fun handleVideoResponse(videoResponse: VideoResponse) {
        if (videoResponse.isSuccess) {
            playerViewModel.uris = videoResponse.uris
            playerViewModel.backupUri = videoResponse.backupUri

            withContext(Dispatchers.Main) {
                progressBar.visibility = ProgressBar.GONE;
                showSourceControls();
                initializePlayer();
            }
        } else {
            when (videoResponse.errorCode) {
                VideoResponse.ERROR_LINKS_NOT_FOUND -> {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@PlayerActivity, R.string.player_error_links_notfound, Toast.LENGTH_SHORT).show();
                        switchToWebPlayer();
                        if(playerViewModel.mirrors.isNotEmpty()) {
                            showMirrorSelectionDialog();
                        } else {
                            finish();
                        }

                    }
                }
                VideoResponse.ERROR_SOURCE_NOT_FOUND -> {
                    Toast.makeText(this@PlayerActivity, R.string.player_error_source_not_found, Toast.LENGTH_SHORT).show();
                    switchToWebPlayer();
                    if(playerViewModel.mirrors.isNotEmpty()) {
                        showMirrorSelectionDialog();
                    } else {
                        finish();
                    }

                }
                VideoResponse.ERROR_TIMEOUT -> Toast.makeText(this@PlayerActivity, R.string.player_error_request_timeout, Toast.LENGTH_SHORT).show()
                else -> withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@PlayerActivity,
                        R.string.player_error_episode_unknown,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun getNumCores(): Int {
        // Private Class to display only CPU devices in the directory listing
        class CpuFilter : FileFilter {
            override fun accept(pathname: File): Boolean {
                // Check if filename is "cpu", followed by a single digit number
                return Pattern.matches("cpu[0-9]+", pathname.name)
            }
        }
        return try {
            // Get directory containing CPU info
            val dir = File("/sys/devices/system/cpu/")
            // Filter to only list the devices we care about
            val files: Array<File> = dir.listFiles(CpuFilter())
            // Return the number of cores (virtual CPU devices)
            files.size
        } catch (e: Exception) {
            // Default to return 1 core
            1
        }
    }


    override fun onResume() {
        super.onResume()
        playerViewModel.resume()
        playerViewModel.playerHidden = false
        if (!playerViewModel.stopwatch.isRunning) {
            playerViewModel.stopwatch.start()
        }
    }

    override fun onPause() {
        super.onPause()
        playerViewModel.player.playWhenReady = false
        playerViewModel.currentTime = playerViewModel.player.currentPosition
        playerViewModel.playerHidden = true
        if (playerViewModel.stopwatch.isRunning) {
            playerViewModel.stopwatch.stop()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        playerViewModel.player.release()
        val timeWatched = playerViewModel.stopwatch.elapsed(TimeUnit.SECONDS)
        playerViewModel.stopwatch.reset()

        if (timeWatched > 0) {
//            logEpisodeWatched(timeWatched)
        }
    }

    private fun switchToWebPlayer() {
        var webPlayerLink = ""
        if ("" != playerViewModel.backupUri) {
            webPlayerLink = playerViewModel.backupUri
        } else if ("" != playerViewModel.episode.uri) {
            webPlayerLink = playerViewModel.episode.uri
        }

        if ("" != webPlayerLink) {
            Toast.makeText(this, R.string.player_web_switching, Toast.LENGTH_SHORT).show()
            val intent = Intent(this, WebPlayerActivity::class.java)
            intent.putExtra("link", webPlayerLink)
            startActivity(intent)
        }

    }

    private fun showResolutionSelectionDialog() {
        val videoLinkItems = arrayOfNulls<CharSequence>(playerViewModel.uris.size)
        for (i in playerViewModel.uris.indices) {
            videoLinkItems[i] = playerViewModel.uris[i].name
        }
        val videoLinkDialog: AlertDialog = AlertDialog.Builder(this)
            .setItems(videoLinkItems) { dialogInterface, i ->
                playerViewModel.currentTime = playerViewModel.player.currentPosition
                playUrl(playerViewModel.uris[i])
                resolutionSwitcher.text = videoLinkItems[i]
                playerViewModel.resume()
                dialogInterface.dismiss()
            }
            .create()
        videoLinkDialog.show()

    }

    private fun playUrl(video: Video) {
        Toast.makeText(this, R.string.player_video_loading, Toast.LENGTH_LONG).show()
        val uri: Uri = Uri.parse(video.uri)

        val dataSourceFactory: DataSource.Factory = getAppropriateDSF(uri)

        val mediaSource: MediaSource? = when (video.format) {
            Video.FORMAT_HLS -> HlsMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
            Video.FORMAT_DASH -> {
                val dashChunkSourceFactory: DashChunkSource.Factory =
                    DefaultDashChunkSource.Factory(dataSourceFactory)
                DashMediaSource.Factory(dashChunkSourceFactory, dataSourceFactory)
                    .createMediaSource(uri)
            }
            Video.FORMAT_MP4 -> ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(
                uri
            )
            else -> null
        }

        playerViewModel.player.prepare(mediaSource!!)
        playerViewModel.player.playWhenReady = true

    }

    private fun getAppropriateDSF(uri: Uri): DataSource.Factory {
        val defaultSchemes = arrayOf("file")
        for (scheme in defaultSchemes) {
            if (scheme == uri.scheme) {
                return playerViewModel.defaultDataSourceFactory!!
            }
        }

        return playerViewModel.okDataSourceFactory!!
    }

    private fun showMirrorSelectionDialog() {
        val mirrorItems = arrayOfNulls<CharSequence>(playerViewModel.mirrors.size)
        for (i in playerViewModel.mirrors.indices) {
            mirrorItems[i] = playerViewModel.mirrors[i].name
        }
        val mirrorsDialog: AlertDialog = AlertDialog.Builder(this)
            .setTitle(R.string.dialog_player_mirror_choice)
            .setItems(mirrorItems) { dialogInterface, i ->
                CoroutineScope(Dispatchers.Default).launch {
                    val videos = playerViewModel.getVideoUrisFromMirror(playerViewModel.mirrors[i])
                    withContext(Dispatchers.Main) {
                        handleVideoResponse(videos!!)
                        playerViewModel.resumeFromMirror = playerViewModel.player.currentPosition
                        dialogInterface.dismiss()
                        progressBar.visibility = ProgressBar.VISIBLE
                    }
                }
            }
            .setNegativeButton(
                R.string.dialog_cancel
            ) { dialogInterface, _ ->
                dialogInterface.cancel()
                progressBar.visibility = ProgressBar.GONE
            }
            .setCancelable(false)
            .create()
        mirrorsDialog.show()

    }

    private fun hideSourceControls() {
        resolutionSwitcher.visibility = TextView.GONE
        mirrorSelector.visibility = TextView.GONE
        viewerSwitcher.visibility = Button.GONE
    }

    private fun showSourceControls() {
        resolutionSwitcher.visibility = TextView.VISIBLE
        mirrorSelector.visibility = TextView.VISIBLE
        viewerSwitcher.visibility = Button.VISIBLE
    }

    private fun setFullscreen() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }

    private fun getEventListener(): Player.EventListener {
        return object : Player.EventListener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if (playbackState == Player.STATE_BUFFERING) {
                    //Toast.makeText(getActivity(), "Buffering", Toast.LENGTH_SHORT).show();
                    progressBar.visibility = ProgressBar.VISIBLE
                    if (playerViewModel.stopwatch.isRunning()) {
                        playerViewModel.stopwatch.stop()
                    }
                }
                if (playbackState == Player.STATE_READY) {
                    if (!playerViewModel.stopwatch.isRunning()) {
                        playerViewModel.stopwatch.start()
                    }
                    if (playerViewModel.playerHidden) {
                        playerViewModel.player.setPlayWhenReady(false)
                    }
//                    if (!resumedFromHistory) {
//                        val episodes: HashMap<String, Int> =
//                            library.getEpisodesInHistory(playerViewModel.series.getSource(), playerViewModel.series.getId())
//                        val timeMark = episodes[playerViewModel.episode.getId()]
//                        if (null != timeMark) {
//                            if (timeMark > playerViewModel.player.getDuration()) {
//                                playerViewModel.player.seekTo(0)
//                            } else {
//                                playerViewModel.player.seekTo(timeMark as Long)
//                            }
//                        }
//                        resumedFromHistory = true
//                    }
                    if (playerViewModel.resumeFromMirror != 0L) {
                        playerViewModel.currentTime = playerViewModel.resumeFromMirror
                        playerViewModel.resume()
                        playerViewModel.resumeFromMirror = 0
                    }
                    if (null != this@PlayerActivity) {
                        if (playWhenReady) {
                            this@PlayerActivity.window
                                .addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                            //Toast.makeText(getActivity(), "Ready", Toast.LENGTH_SHORT).show();
                        } else {
                            this@PlayerActivity.window
                                .clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                            //Toast.makeText(getActivity(), "Paused", Toast.LENGTH_SHORT).show();
//                            logHistory()
                        }
                    }
                    progressBar.visibility = ProgressBar.GONE
//                    playerViewModel.adsManager.tryPlayAd(this@PlayerActivity)
                }
            }

            override fun onPlayerError(error: ExoPlaybackException) {
                if (error.type == ExoPlaybackException.TYPE_SOURCE) {
                    //Toast.makeText(getActivity(), "Source error: " + error.getSourceException().getMessage(), Toast.LENGTH_SHORT).show();
                    if (playerViewModel.mirrors.size > 0) {
                        Toast.makeText(
                            this@PlayerActivity,
                            R.string.player_error_mirror_broken,
                            Toast.LENGTH_SHORT
                        ).show()
                        showMirrorSelectionDialog()
                    } else {
                        Toast.makeText(
                            this@PlayerActivity,
                            R.string.player_error_episode_broken,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    companion object {
        private const val TAG = "PlayerActivity"
    }

}