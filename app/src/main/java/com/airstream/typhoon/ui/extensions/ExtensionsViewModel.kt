package com.airstream.typhoon.ui.extensions

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.airstream.typhoon.extension.ExtensionHolder
import com.airstream.typhoon.utils.Injector

class ExtensionsViewModel(application: Application) : AndroidViewModel(application) {

    private val extensionsManager = Injector.getExtensionManager(application)

    val installedExtensions = extensionsManager.getInstalledExtensions()
    val installableExtensions = extensionsManager.getInstallableExtensions()

}