package com.example.androidappfilmproject.domain

import androidx.paging.PagingData
import com.example.androidappfilmproject.data.MainRepository
import com.example.androidappfilmproject.data.entity.Film
import com.example.androidappfilmproject.data.entity.toFilm
import com.example.androidappfilmproject.data.preferences.PreferenceProvider
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

// Класс-интерфейс для взаимодействия с данными (посредником между ViewModel и Repository)
class Interactor(
    private val repo: MainRepository, // Инициализируем репозиторий
    private val preferences: PreferenceProvider // Инициализируем PreferenceProvider
) {

    // Инициализируем Observable для получения состояния загрузки
    val loadingStatus: Observable<Boolean> = repo.getLoadingStatus()

    // Инициализируем метод для обновления состояния загрузки
    fun setLoadingStatus(isLoading: Boolean) = repo.setLoadingStatus(isLoading)

    // Метод для получения Observable для получения категории
    fun getCategoryPreferenceObservable(): Observable<String> = preferences.getCategoryObservable()

    // Метод для сохранения категории в SharedPreferences
    fun saveDefaultCategoryToPreferences(category: String) {
        preferences.saveDefaultCategory(category)
    }

    // Метод для конвертации ответа от API (TmdbResults DTO) в список объектов Film (Entity)
    // с помощью оператора .map() RxJava.
    fun getRecommendation(category: String): Observable<List<Film>> {
        return repo.getFilmsFromApiRx(category)
            .map { tmdbResults ->
                // Используем оператор map для трансформации данных прямо в потоке
                tmdbResults.tmdbFilms.map { it.toFilm() }
            }
    }

    // Метод для получения для постраничной загрузки фильмов
    fun getPagedFilms(category: String): Observable<PagingData<Film>> = repo.getFilms(category)

    // Метод для получения постраничных результатов поиска.
    fun getSearchResult(query: String): Observable<PagingData<Film>> = repo.getSearchResult(query)

    // Метод для получения всех фильмов из базы данных (для демо-режима).
    fun getAllFilmsFromDb(): Observable<List<Film>> = repo.getAllFilmsFromDb()

    // Метод для получения постраничного списка избранных фильмов.
    fun getFavoriteFilmsPaging(): Observable<PagingData<Film>> = repo.getFavoriteFilmsPaging()

    // Метод для получения фильма по ID.
    fun getFilmById(id: Int): Observable<Film> = repo.getFilmById(id)

    // Метод для обновления статуса "избранное"
    fun toggleFavoriteStatus(film: Film): Completable {
        val updatedFilm = film.copy(isInFavorites = !film.isInFavorites)
        return repo.updateFilm(updatedFilm)
    }

    // Метод для удаления фильма из кэша
    fun removeFilmFromCache(film: Film): Completable = repo.deleteFilm(film)
}
