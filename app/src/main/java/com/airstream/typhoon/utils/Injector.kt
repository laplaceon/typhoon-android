package com.airstream.typhoon.utils

import android.content.Context
import com.airstream.typhoon.data.ExtensionManager
import com.airstream.typhoon.data.SeriesRepository
import com.airstream.typhoon.data.SourceManager
import com.airstream.typhoon.data.SourceRepository

object Injector {

    fun getExtensionManager(ctx: Context): ExtensionManager {
        return ExtensionManager.getInstance(ctx.applicationContext)
    }

    fun getSourceManager(ctx: Context): SourceManager {
        return SourceManager.getInstance(ctx.applicationContext)
    }

    fun getSourceRepository(ctx: Context): SourceRepository {
        return SourceRepository.getInstance(SourceManager.getInstance(ctx.applicationContext))
    }

}