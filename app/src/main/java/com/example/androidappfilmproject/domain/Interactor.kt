package com.example.androidappfilmproject.domain

import androidx.paging.PagingData
import com.example.androidappfilmproject.data.MainRepository
import com.example.androidappfilmproject.data.entity.Film
import com.example.androidappfilmproject.data.preferences.PreferenceProvider
import kotlinx.coroutines.flow.Flow

// Создаем класс Interactor, который является посредником между ViewModel и Repository.
// Он содержит определяет какой источник данных использовать.
class Interactor(
    private val repo: MainRepository,
    private val preferences: PreferenceProvider
) {
    // Работа с категориями (подборками)
    // Метод для получения потока с сохраненной категорией фильмов.
    fun getCategoryPreferenceFlow(): Flow<String> = preferences.categoryFlow

    // Метод для сохранения категории фильмов в SharedPreferences.
    fun saveDefaultCategoryToPreferences(category: String) {
        preferences.saveDefaultCategory(category)
    }

    // Работа с данными (через репозиторий)

    // Метод для получения постраничного списка фильмов.
    fun getPagedFilms(category: String): Flow<PagingData<Film>> {
        return repo.getFilms(category)
    }

    // Метод для получения постраничных результатов поиска.
    fun getSearchResult(query: String): Flow<PagingData<Film>> {
        return repo.getSearchResult(query)
    }

    // Метод для получения всех фильмов из локальной базы данных (для демо-режима).
    fun getAllFilmsFromDb(): Flow<List<Film>> {
        return repo.getAllFilmsFromDb()
    }

    // Метод для получения постраничного списка избранных фильмов.
    fun getFavoriteFilmsPaging(): Flow<PagingData<Film>> {
        return repo.getFavoriteFilmsPaging()
    }
    
    // Метод для получения фильма по ID.
    fun getFilmById(id: Int): Flow<Film> {
        return repo.getFilmById(id)
    }

    // Метод для переключения статуса "избранное" у фильма.
    suspend fun toggleFavoriteStatus(film: Film) {
        val updatedFilm = film.copy(isInFavorites = !film.isInFavorites)
        repo.updateFilm(updatedFilm)
    }

    // Метод для удаления фильма из кэша (БД).
    suspend fun removeFilmFromCache(film: Film) {
        repo.deleteFilm(film)
    }
}
