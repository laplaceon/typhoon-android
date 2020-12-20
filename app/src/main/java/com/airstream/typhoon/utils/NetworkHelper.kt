package com.airstream.typhoon.utils

import android.content.Context
import com.uvnode.typhoon.extensions.executor.JSEClient
import okhttp3.Cache
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class NetworkHelper private constructor(ctx: Context) {

    val okClient: OkHttpClient = OkHttpClient.Builder()
        .cache(Cache(ctx.cacheDir, 10 * 10 * 1024))
        .cookieJar(SyncCookieHandler())
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    val jseClient: JSEClient = JSEClient(ctx)

    companion object {
        @Volatile private var instance: NetworkHelper? = null

        fun getInstance(ctx: Context) =
            instance ?: synchronized(this) {
                instance ?: NetworkHelper(ctx).also { instance = it }
            }
    }
}