package com.example.androidappfilmproject.domain

import androidx.paging.PagingData
import com.example.androidappfilmproject.data.MainRepository
import com.example.androidappfilmproject.data.TmdbApi
import kotlinx.coroutines.flow.Flow

// Создаем класс Interactor, который является посредником между ViewModel и Repository.
// Он содержит определяет какой источник данных использовать.
class Interactor(private val repo: MainRepository, private val retrofitService: TmdbApi) {
    // Метод для получения списка фильмов.
    fun getFilms(): Flow<PagingData<Film>> {
        return repo.getFilms()
    }

    // Метод для получения результатов поиска.
    fun getSearchResult(query: String): Flow<PagingData<Film>> {
        return if (query.isBlank()) { // Если запрос пустой, возвращаем все фильмы.
            getFilms()
        } else {
            repo.getSearchResult(query) // Иначе выполняем поиск.
        }
    }

    // Метод для переключения статуса "избранное" у фильма.
    suspend fun toggleFavoriteStatus(film: Film) {
        repo.toggleFavoriteStatus(film)
    }

    // Метод для получения избранных фильмов с пагинацией.
    fun getFavoriteFilmsPaging(): Flow<PagingData<Film>> {
        return repo.getFavoriteFilmsPaging()
    }

    // Метод для получения фильма по ID.
    fun getFilmById(id: Int): Flow<Film> {
        return repo.getFilmById(id)
    }

    // Метод для получения всех фильмов из локальной базы данных (для демо-режима).
    fun getAllFilmsFromDb(): Flow<List<Film>> {
        return repo.getAllFilmsFromDb()
    }
}
