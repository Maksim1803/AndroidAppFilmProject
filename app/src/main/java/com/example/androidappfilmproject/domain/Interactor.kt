package com.example.androidappfilmproject.domain

import androidx.paging.PagingData
import com.example.androidappfilmproject.data.MainRepository
import com.example.androidappfilmproject.data.preferences.PreferenceProvider
import kotlinx.coroutines.flow.Flow

class Interactor(
    private val repo: MainRepository,
    private val preferences: PreferenceProvider
) {
    // Работа с категориями (подборками)
    fun getCategoryPreferenceFlow(): Flow<String> = preferences.categoryFlow

    fun saveDefaultCategoryToPreferences(category: String) {
        preferences.saveDefaultCategory(category)
    }

    // Работа с данными (через репозиторий)

    fun getPagedFilms(category: String): Flow<PagingData<Film>> {
        return repo.getFilms(category)
    }

    fun getSearchResult(query: String): Flow<PagingData<Film>> {
        return repo.getSearchResult(query)
    }

    // Добавляем недостающий метод для демо-режима
    fun getAllFilmsFromDb(): Flow<List<Film>> {
        return repo.getAllFilmsFromDb()
    }

    fun getFavoriteFilmsPaging(): Flow<PagingData<Film>> {
        return repo.getFavoriteFilmsPaging()
    }

    fun getFilmById(id: Int): Flow<Film> {
        return repo.getFilmById(id)
    }

    suspend fun toggleFavoriteStatus(film: Film) {
        val updatedFilm = film.copy(isInFavorites = !film.isInFavorites)
        repo.updateFilm(updatedFilm)
    }
}
