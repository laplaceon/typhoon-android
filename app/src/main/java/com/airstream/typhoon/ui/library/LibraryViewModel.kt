package com.airstream.typhoon.ui.library

import android.app.Application
import androidx.lifecycle.*
import com.airstream.typhoon.data.library.entities.Category
import com.airstream.typhoon.data.library.entities.Series
import com.airstream.typhoon.utils.Injector

class LibraryViewModel(application: Application) : AndroidViewModel(application) {

    private val library = Injector.getLibrary(application.applicationContext)
    val categoryDao = library.categoryDao()
    val seriesDao = library.seriesDao()

    private val _selectedCategory: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>(0)
    }

    val selectedCategory: LiveData<Int> = _selectedCategory

    val categories: LiveData<List<Category>> = categoryDao.getAll().asLiveData()

    fun setCategory(pos: Int) {
        _selectedCategory.value = pos
    }


}