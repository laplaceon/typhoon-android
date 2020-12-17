package com.airstream.typhoon.ui.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    var q = ""
    var page = -1
}