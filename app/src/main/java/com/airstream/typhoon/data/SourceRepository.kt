package com.airstream.typhoon.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.uvnode.typhoon.extensions.source.MetaSource

class SourceRepository private constructor(private val sourceManager: SourceManager) {

    private val _sources: MutableLiveData<List<MetaSource>> by lazy {
        MutableLiveData<List<MetaSource>>()
    }

    val sources: LiveData<List<MetaSource>> = _sources

    val sourcesMap: MutableMap<String, Int> = mutableMapOf()

    var currentSource: String = ""

    fun getSourcesList(): LiveData<List<MetaSource>> {
        if (_sources.value == null) {
            sourceManager.getSources().also { it ->
                _sources.value = it
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

    fun getSourceById(id: String?) = sourcesMap[id]?.let { sources.value?.get(it) }

    fun reload() {
        sourceManager.getSources()
    }

    companion object {
        private const val TAG = "SourceRepository"

        @Volatile private var instance: SourceRepository? = null;

        fun getInstance(sourceManager: SourceManager) =
            instance ?: synchronized(this) {
                instance ?: SourceRepository(sourceManager).also { instance = it }
            }
    }
}