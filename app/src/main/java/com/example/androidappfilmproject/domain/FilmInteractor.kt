package com.example.androidappfilmproject.domain

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

//Создаем интерфейс FilmInteractor, который  определяет,
// какие операции с данными о фильмах доступны в приложении,
// и скрывает детали их реализации от ViewModel.
interface FilmInteractor {
    // Получение списка фильмов с пагинацией
    fun getFilms(): Flow<PagingData<Film>>
    // Получение результатов поиска фильмов с пагинацией
    fun getSearchResult(query: String): Flow<PagingData<Film>>
    // Переключение статуса "избранное" для фильма
    suspend fun toggleFavoriteStatus(film: Film)
    // Получение списка избранных фильмов с пагинацией
    fun getFavoriteFilmsPaging(): Flow<PagingData<Film>>
    // Получение фильма по его ID
    fun getFilmById(id: Int): Flow<Film>
    // Получение всех фильмов из базы данных
    fun getAllFilmsFromDb(): Flow<List<Film>>
}
