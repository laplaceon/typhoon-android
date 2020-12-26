package com.airstream.typhoon.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.uvnode.typhoon.extensions.model.Filter
import com.uvnode.typhoon.extensions.model.Ranking
import com.uvnode.typhoon.extensions.source.MetaSource

class SourceRepository private constructor(private val sourceManager: SourceManager) {

    val sources = sourceManager.getSources()

    val sourcesMap = sourceManager.sourcesMap

    val currentSource: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    fun getSourceByPos(pos: Int) = sources.value?.get(pos)

    fun getSourceById(id: String) = sourcesMap[id]?.let { sources.value?.get(it) }

    fun setCurrentSource(id: String?) {
        currentSource.value = id
    }

    fun getFilters(sourceById: MetaSource?): List<Filter<*>>? = sourceManager.getFilters(sourceById)

    companion object {
        private const val TAG = "SourceRepository"

        @Volatile private var instance: SourceRepository? = null;

        fun getInstance(sourceManager: SourceManager) =
            instance ?: synchronized(this) {
                instance ?: SourceRepository(sourceManager).also { instance = it }
            }
    }
}