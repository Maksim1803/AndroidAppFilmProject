package com.example.androidappfilmproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.rxjava3.cachedIn
import com.example.androidappfilmproject.data.entity.Film
import com.example.androidappfilmproject.domain.Interactor
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

// Создаем класс HomeFragmentViewModel, который является ViewModel для HomeFragment.
class HomeFragmentViewModel @Inject constructor(
    private val interactor: Interactor
) : ViewModel() {

    // Инициализируем CompositeDisposable для управления подписками
    private val compositeDisposable = CompositeDisposable()

    // Инициализируем BehaviorSubject для хранения поискового запроса
    private val querySubject = BehaviorSubject.createDefault("")

    // Инициализируем Observable для показа ProgressBar
    val showProgressBar: Observable<Boolean> = interactor.loadingStatus

    // Инициализируем Observable для получения списка фильмов
    val films: Observable<PagingData<Film>>
    
    // Новый поток для рекомендации фильма
    val recommendation: Observable<Film>

    init {
        val categoryObservable = interactor.getCategoryPreferenceObservable()
            .distinctUntilChanged()

        // Инициализируем поток рекомендации: берем первый фильм из списка
        recommendation = categoryObservable
            .switchMap { category ->
                interactor.getRecommendation(category)
                    .subscribeOn(Schedulers.io())
                    .onErrorResumeNext { Observable.empty() }
            }
            .filter { it.isNotEmpty() }
            .map { it.first() } // Берем самый популярный
            .observeOn(AndroidSchedulers.mainThread())

        // Основной поток фильмов
        films = Observable.combineLatest(
            categoryObservable,
            querySubject.distinctUntilChanged()
        ) { category, query ->
            category to query
        }
        .debounce(300, TimeUnit.MILLISECONDS)
        .switchMap { (category, query) ->
            if (query.isEmpty()) {
                interactor.getPagedFilms(category)
            } else {
                interactor.getSearchResult(query)
            }
        }
        .cachedIn(viewModelScope)
    }

    // Метод для установки нового поискового запроса
    fun setQuery(newQuery: String) {
        querySubject.onNext(newQuery)
    }

    // Метод для обработки клика по иконке "избранное"
    fun onFavoriteClicked(film: Film) {
        val disposable = interactor.toggleFavoriteStatus(film).subscribe()
        compositeDisposable.add(disposable)
    }

    // Метод для удаления фильма из кэша
    fun removeFilmFromCache(film: Film) {
        val disposable = interactor.removeFilmFromCache(film).subscribe()
        compositeDisposable.add(disposable)
    }

    // Метод для управления видимостью ProgressBar
    fun toggleProgressBar(isVisible: Boolean) {
        interactor.setLoadingStatus(isVisible)
    }
    // Вызывается при уничтожении ViewModel
    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}
