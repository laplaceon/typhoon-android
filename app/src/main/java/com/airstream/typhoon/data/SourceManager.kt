package com.airstream.typhoon.data

import android.content.Context
import com.airstream.typhoon.utils.Injector
import com.airstream.typhoon.utils.SyncCookieHandler
import com.uvnode.typhoon.extensions.api.ApiCallbacks
import com.uvnode.typhoon.extensions.api.ApiError
import com.uvnode.typhoon.extensions.api.ApiResponse
import com.uvnode.typhoon.extensions.executor.JSEClient
import com.uvnode.typhoon.extensions.model.Series
import com.uvnode.typhoon.extensions.model.Source
import com.uvnode.typhoon.extensions.source.MetaSource
import okhttp3.Cache
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class SourceManager private constructor(ctx: Context) {

    var okClient: OkHttpClient = OkHttpClient.Builder()
        .cache(Cache(ctx.cacheDir, 10 * 10 * 1024))
        .cookieJar(SyncCookieHandler())
        .readTimeout(20, TimeUnit.SECONDS)
        .build()
        private set

    var jseClient: JSEClient = JSEClient(ctx)
        private set

    private val extensionManager = Injector.getExtensionManager(ctx)

    fun getSources(): List<MetaSource> {
        val extensions = extensionManager.getInstalledExtensions()

        var sources = mutableListOf<MetaSource>()

        for(extension in extensions) {
            extension.extension?.sources?.let { sources.addAll(it) }
        }

        return sources
    }

    suspend fun getSeriesList(source: MetaSource): List<Series>? =
        suspendCoroutine {
            val callback = object: ApiCallbacks {
                override fun onFailure(error: ApiError?) {
                    it.resume(null)
                }

                override fun onResponse(response: ApiResponse?) {
                    val series: List<Series> = response?.get() as List<Series>
                    it.resume(series)
                }
            }

            source.getSeriesList(callback)
        }

    companion object {
        private var instance: SourceManager? = null;

        fun getInstance(ctx: Context) =
            instance ?: synchronized(this) {
                instance ?: SourceManager(ctx).also { instance = it }
            }
    }

}