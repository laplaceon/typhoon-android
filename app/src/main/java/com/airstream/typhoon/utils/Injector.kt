package com.airstream.typhoon.utils

import android.content.Context
import com.airstream.typhoon.data.SeriesRepository
import com.airstream.typhoon.extension.ExtensionManager
import com.airstream.typhoon.data.SourceManager
import com.airstream.typhoon.data.SourceRepository

object Injector {

    fun getNetworkHelper(ctx: Context): NetworkHelper {
        return NetworkHelper.getInstance(ctx.applicationContext)
    }

    fun getExtensionManager(ctx: Context): ExtensionManager {
        return ExtensionManager.getInstance(ctx.applicationContext)
    }

    fun getSourceRepository(ctx: Context): SourceRepository {
        return SourceRepository.getInstance(SourceManager(ctx.applicationContext))
    }

    fun getSeriesRepository(ctx: Context): SeriesRepository {
        return SeriesRepository.getInstance(SourceManager(ctx.applicationContext))
    }

}