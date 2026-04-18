package com.example.androidappfilmproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.rxjava3.cachedIn
import com.example.androidappfilmproject.domain.Interactor
import com.example.database_module.entity.Film
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

    // Инициализируем Observable для получения списка фильмов. Используем Film из database_module
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

        // Основной поток фильмов с поддержкой поиска и пагинации
        films = Observable.combineLatest(
            categoryObservable,
            querySubject
                .distinctUntilChanged()
                // Фильтруем: пропускаем пустую строку (для сброса) или текст от 3 символов
                .filter { it.isEmpty() || it.length >= 3 }
                // Задержка, чтобы не спамить запросами при вводе
                .debounce(500, TimeUnit.MILLISECONDS)
        ) { category, query ->
            category to query
        }
            // Метод отменяющий предыдущие подписки и
            // отвечающий за отображение только последнего запроса
            .switchMap { (category, query) ->
                if (query.isEmpty()) {
                    // Если поиск пуст — грузим фильмы по категориям из БД + Сети
                    interactor.getPagedFilms(category)
                } else {
                    // Если есть запрос — грузим результаты поиска из API (с пагинацией)
                    interactor.getSearchResult(query)
                }
            }
            // Кэшируем результат в viewModelScope, чтобы не терять данные при повороте экрана
            .cachedIn(viewModelScope)
    }

    // Метод для установки нового поискового запроса
    fun setQuery(newQuery: String) {
        querySubject.onNext(newQuery)
    }

    // Метод для обработки клика по иконке "избранное"
    fun onFavoriteClicked(film: Film) {
        // ИСПРАВЛЕНИЕ: Переносим работу с БД в IO поток и добавляем обработку ошибок
        val disposable = interactor.toggleFavoriteStatus(film)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                // Успешно обновили
            }, {
                // Логируем ошибку, но приложение не падает
                it.printStackTrace()
            })
        compositeDisposable.add(disposable)
    }

    // Метод для удаления фильма из кэша
    fun removeFilmFromCache(film: Film) {
        val disposable = interactor.removeFilmFromCache(film)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({}, { it.printStackTrace() })
        compositeDisposable.add(disposable)
    }

    // Метод для управления видимостью ProgressBar
    fun toggleProgressBar(isVisible: Boolean) {
        interactor.setLoadingStatus(isVisible)
    }

    // Метод для очистки кэша при смене языка
    fun clearCache() {
        val disposable = interactor.clearCache()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
        compositeDisposable.add(disposable)
    }

    // Вызывается при уничтожении ViewModel
    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}
