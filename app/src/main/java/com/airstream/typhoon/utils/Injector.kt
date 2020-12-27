package com.airstream.typhoon.utils

import android.content.Context
import com.airstream.typhoon.data.SeriesRepository
import com.airstream.typhoon.extension.ExtensionManager
import com.airstream.typhoon.data.SourceManager
import com.airstream.typhoon.data.SourceRepository
import com.airstream.typhoon.data.library.Library

object Injector {

    fun getNetworkHelper(ctx: Context) = NetworkHelper.getInstance(ctx.applicationContext)

    fun getExtensionManager(ctx: Context) = ExtensionManager.getInstance(ctx.applicationContext)

    fun getSourceRepository(ctx: Context) = SourceRepository.getInstance(SourceManager(ctx.applicationContext))

    fun getSeriesRepository(ctx: Context) = SeriesRepository.getInstance(SourceManager(ctx.applicationContext))

    fun getLibrary(ctx: Context) = Library.getInstance(ctx.applicationContext)

}