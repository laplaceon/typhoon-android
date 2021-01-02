package com.airstream.typhoon.ui.extensions

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.airstream.typhoon.extension.ExtensionHolder
import com.airstream.typhoon.utils.Injector

class ExtensionsViewModel(application: Application) : AndroidViewModel(application) {

    private val extensionsManager = Injector.getExtensionManager(application)

    private val installedExtensions = extensionsManager.getInstalledExtensions()
    private val installableExtensions = extensionsManager.getInstallableExtensions()

    val extensions: MediatorLiveData<List<ExtensionHolder>> = MediatorLiveData()

    init {
        extensions.addSource(installedExtensions) {
            if (installableExtensions.value != null) {
                extensions.value = installedExtensions.value!! + installableExtensions.value!!
            } else {
                extensions.value = installedExtensions.value
            }
        }

        extensions.addSource(installableExtensions) {
            if (it != null) {
                extensions.value = installedExtensions.value!! + installableExtensions.value!!
            } else {
                extensions.value = installedExtensions.value
            }
        }
    }

    companion object {
        private const val TAG = "ExtensionsViewModel"
    }

}
