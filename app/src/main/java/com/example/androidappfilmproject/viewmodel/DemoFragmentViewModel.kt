package com.example.androidappfilmproject.viewmodel

import androidx.lifecycle.ViewModel
import com.example.androidappfilmproject.data.entity.Film
import com.example.androidappfilmproject.domain.Interactor
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject

// Создаем класс DemoFragmentViewModel, который является ViewModel для DemoFragment.
class DemoFragmentViewModel @Inject constructor(
    interactor: Interactor
) : ViewModel() {

    // Инициализируем BehaviorSubject для хранения поискового запроса
    private val querySubject = BehaviorSubject.createDefault("")

    // Поток отфильтрованных фильмов
    val films: Observable<List<Film>> = Observable.combineLatest(
        interactor.getAllFilmsFromDb(),
        querySubject.distinctUntilChanged().debounce(300, TimeUnit.MILLISECONDS)
    ) { allFilms, query ->
        if (query.isEmpty()) {
            allFilms
        } else {
            allFilms.filter { 
                it.title.lowercase(Locale.getDefault()).contains(query.lowercase(Locale.getDefault())) 
            }
        }
    }
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())

    // Метод для установки нового поискового запроса
    fun setQuery(query: String) {
        querySubject.onNext(query)
    }
}
