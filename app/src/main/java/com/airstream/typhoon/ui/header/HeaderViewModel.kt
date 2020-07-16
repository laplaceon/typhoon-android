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

class HeaderViewModel(application: Application) : AndroidViewModel(application) {

    private val sourceRepository = Injector.getSourceRepository(application)
    private val preferences = PreferenceManager.getDefaultSharedPreferences(application)
//    private val sourceNames: List<String>
    private var currentSource = getCurrentSourceIndex()

    fun getSources() = sourceRepository.getSourcesList()

    fun getSourceNames() = sourceRepository.getSourceNames()

    fun getCurrentSourceIndex() = sourceRepository.sourcesMap[preferences.getString(SOURCE_KEY, "")] ?: 0

    fun setCurrentSource(pos: Int) {
        if (currentSource != pos) {
            val preferencesEditor = preferences.edit()
            val id = sourceRepository.getSourcesList().value?.get(pos)!!.source.id
            Log.d(TAG, "setCurrentSource: " + id)
            preferencesEditor.putString(SOURCE_KEY, id)
            preferencesEditor.apply()
            currentSource = pos
            sourceRepository.currentSource.value = id
        }
    }

    companion object {
        private const val TAG = "HeaderViewModel"
        private const val SOURCE_KEY = "source"
    }
}