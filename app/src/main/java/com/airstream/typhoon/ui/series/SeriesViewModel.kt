package com.airstream.typhoon.ui.series

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.airstream.typhoon.utils.Injector
import com.uvnode.typhoon.extensions.model.Series

class SeriesViewModel(application: Application) : AndroidViewModel(application) {

    private val seriesRepository = Injector.getSeriesRepository(application)

    var series: Series? = null
    var sourceId: String? = null
}