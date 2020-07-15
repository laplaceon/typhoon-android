package com.airstream.typhoon.data

import com.uvnode.typhoon.extensions.model.Source
import com.uvnode.typhoon.extensions.source.MetaSource

class SourceRepository private constructor(private val sourceManager: SourceManager) {

    private var sources: List<MetaSource>? = null
    private var sourcesMap: MutableMap<String, Int> = mutableMapOf()

    fun getSources(): List<MetaSource> {
        return sources ?: sourceManager.getSources().also {
            sources = it
            sourcesMap.clear()
            it.withIndex().forEach {
                sourcesMap.put(it.value.source.id, it.index)
            }
        }
    }

    fun getSourceNames() = getSources().map { it.source.name }

    companion object {
        @Volatile private var instance: SourceRepository? = null;

        fun getInstance(sourceManager: SourceManager) =
            instance ?: synchronized(this) {
                instance ?: SourceRepository(sourceManager).also { instance = it }
            }
    }
}