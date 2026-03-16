package com.example.androidappfilmproject.viewmodel

import androidx.lifecycle.ViewModel
import com.example.androidappfilmproject.domain.Interactor
import com.example.database_module.entity.Film
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

// Класс ViewModel для экрана "Посмотреть позже"
class WatchLaterViewModel @Inject constructor(private val interactor: Interactor) : ViewModel() {

    // Метод для получения списка фильмов из базы данных
    fun getWatchLaterFilms(): Observable<List<Film>> {
        return interactor.getWatchLaterFilms()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}
