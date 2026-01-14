package com.example.androidappfilmproject.data.dao

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.androidappfilmproject.data.entity.Film
import kotlinx.coroutines.flow.Flow

// Создаем интерфейс FilmDao, который определяет методы для доступа к базе данных.
@Dao
interface FilmDao {
    // Метод для вставки списка фильмов в базу данных.
    // При конфликте (например, одинаковый ID) старые фильмы заменяются новыми.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(films: List<Film>)

    // Метод для вставки одного фильма.
    // Если фильм уже есть в БД, он будет проигнорирован.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(film: Film)

    // Метод для обновления данных существующего фильма в базе данных.
    @Update
    suspend fun update(film: Film)

    // Метод для удаления конкретного фильма из базы данных.
    @Delete
    suspend fun delete(film: Film)

    // Метод для полной очистки таблицы с фильмами.
    @Query("DELETE FROM film_table")
    suspend fun deleteAll()

    // Метод для получения всех фильмов из базы данных в виде Flow (поток данных).
    @Query("SELECT * FROM film_table")
    fun getAllFilms(): Flow<List<Film>>

    // Метод для получения всех фильмов из базы данных в виде LiveData (Модуль 41).
    @Query("SELECT * FROM film_table")
    fun getCachedFilms(): LiveData<List<Film>>

    // Метод для получения всех фильмов для постраничной загрузки через Paging Library.
    @Query("SELECT * FROM film_table")
    fun getFilmsPagingSource(): PagingSource<Int, Film>

    // Метод для получения одного фильма по его уникальному идентификатору (ID).
    @Query("SELECT * FROM film_table WHERE id = :id")
    fun getFilmById(id: Int): Flow<Film?>

    // Метод для получения только избранных фильмов для постраничной загрузки.
    @Query("SELECT * FROM film_table WHERE isInFavorites = 1")
    fun getFavoriteFilmsPagingSource(): PagingSource<Int, Film>
}
