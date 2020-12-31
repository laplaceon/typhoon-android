package com.airstream.typhoon.ui.library

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import com.airstream.typhoon.data.library.entities.Category
import com.airstream.typhoon.data.library.entities.Series
import com.airstream.typhoon.utils.Injector

class LibraryManagerViewModel(application: Application) : AndroidViewModel(application) {

    private val library = Injector.getLibrary(application.applicationContext)
    private val categoryDao = library.categoryDao()
    private val seriesDao = library.seriesDao()

    fun getCategories() = categoryDao.getAll().asLiveData()

    fun getCategories(source: String, series: String) = categoryDao.getAllWithSeriesCheck(source, series).asLiveData()

    fun addCategory(name: String) {
        categoryDao.insertAll(Category(name))
    }

    fun addSeriesToCategory(categoryId: Int, series: com.uvnode.typhoon.extensions.model.Series) {
        seriesDao.insertAll(Series(series.id, categoryId, series.source, series.uri, series.title, series.image))
    }

    fun removeSeriesFromCategory(categoryId: Int, sourceId: String, seriesId: String) {
        seriesDao.delete(categoryId, sourceId, seriesId)
    }

    fun renameCategory(id: Int, name: String) {
        categoryDao.renameCategory(id, name)
    }

    fun delete(category: Category) {
        categoryDao.delete(category)
    }
}