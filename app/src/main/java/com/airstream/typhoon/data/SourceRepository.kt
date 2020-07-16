package com.airstream.typhoon.data

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.uvnode.typhoon.extensions.source.MetaSource

class SourceRepository private constructor(private val sourceManager: SourceManager) {

    private val sources = MutableLiveData<List<MetaSource>?>().apply {
        value = null
    }
    val sourcesMap: MutableMap<String, Int> = mutableMapOf()

    val currentSource = MutableLiveData<String?>().apply {
        value = null
    }

    fun getSourcesList(): MutableLiveData<List<MetaSource>?> {
        if (sources.value == null) {
            sourceManager.getSources().also { it ->
                sources.value = it
                sourcesMap.clear()
                it.withIndex().forEach {
                    sourcesMap[it.value.source.id] = it.index
                }
            }
            Log.d(TAG, "getSourcesList: Called")
        }

        Log.d(TAG, "getSourcesList: Cache Called")

        return sources
    }

    fun getSourceById(id: String) = sourcesMap[id]?.let { sources.value?.get(it) }

    fun getSourceNames() = sources.value?.map { it.source.name }

    companion object {
        private const val TAG = "SourceRepository"

        @Volatile private var instance: SourceRepository? = null;

        fun getInstance(sourceManager: SourceManager) =
            instance ?: synchronized(this) {
                instance ?: SourceRepository(sourceManager).also { instance = it }
            }
    }
}