package com.airstream.typhoon.ui.header

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.preference.PreferenceManager
import com.airstream.typhoon.utils.Injector
import com.uvnode.typhoon.extensions.model.Source
import com.uvnode.typhoon.extensions.source.MetaSource

class HeaderViewModel(application: Application) : AndroidViewModel(application) {

    private val sourceRepository = Injector.getSourceRepository(application)
    private val preferences = PreferenceManager.getDefaultSharedPreferences(application)

    val sources = sourceRepository.sources

    val currentSource: LiveData<String> = sourceRepository.currentSource

    fun getSourceNames(sources: List<MetaSource>) = sources.map { it.source.name }

    fun getCurrentSourceIndex() = preferences.getInt(CURRENT_SOURCE_INDEX, 0)

    fun setCurrentSourceByPos(id: String) {
        if (sourceRepository.currentSource.value != id) {
            val preferencesEditor = preferences.edit()
            preferencesEditor.putString(CURRENT_SOURCE_INDEX, id)
            preferencesEditor.apply()
            Log.d(TAG, "setCurrentSource: $id")
            sourceRepository.currentSource.value = id
        }
    }

    fun getCurrentSourceObj(): Source {
        if (getCurrentSourceIndex() > sources.value!!.size) {

        }

        return sources.value?.get(getCurrentSourceIndex())!!.source
    }

    companion object {
        private const val TAG = "HeaderViewModel"
        private const val CURRENT_SOURCE_INDEX = "current_source_index"
    }
}