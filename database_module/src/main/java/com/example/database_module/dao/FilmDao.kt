package com.example.database_module.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.database_module.entity.Film
import io.reactivex.rxjava3.core.Observable

// Интерфейс для доступа к базе данных
@Dao
interface FilmDao {

    // Метод для пакетной вставки фильмов
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(films: List<Film>)

    // Метод для добавления или обновления одного фильма в базу данных
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(film: Film)

    // Метод для обновления данных фильма
    @Update
    fun update(film: Film)

    // Метод для удаления фильма из базы данных
    @Delete
    fun delete(film: Film)

    // Метод для удаления фильмов, если они не "Избранное" и не в "Посмотреть позже"
    @Query("DELETE FROM film_table WHERE category = :category AND isInFavorites = 0 AND isInWatchLater = 0")
    fun deleteUnusedByCategory(category: String)

    // Метод для удаления фильмов конкретной категории (кроме избранных)
    @Query("DELETE FROM film_table WHERE category = :category AND isInFavorites = 0")
    fun deleteByCategory(category: String)

    // Метод для полной очистки таблицы фильмов
    @Query("DELETE FROM film_table")
    fun deleteAll()

    // Метод для получения всех фильмов из базы данных
    @Query("SELECT * FROM film_table")
    fun getAllFilms(): Observable<List<Film>>

    // Метод для получения ID имен всех избранных фильмов
    @Query("SELECT id FROM film_table WHERE isInFavorites = 1")
    fun getFavoriteIds(): List<Int>

    // Метод для получения источника данных пагинации по категории
    @Query("SELECT * FROM film_table WHERE category = :category")
    fun getFilmsPagingSource(category: String): PagingSource<Int, Film>

    // Метод для получения фильма по его ID
    @Query("SELECT * FROM film_table WHERE id = :id")
    fun getFilmById(id: Int): Observable<Film>

    // Метод для получения источника данных пагинации для списка «Избранное»
    @Query("SELECT * FROM film_table WHERE isInFavorites = 1")
    fun getFavoriteFilmsPagingSource(): PagingSource<Int, Film>

    // Метод для получения списка "Посмотреть позже" (реактивный поток)
    @Query("SELECT * FROM film_table WHERE isInWatchLater = 1 ORDER BY watchLaterTime ASC")
    fun getWatchLaterFilms(): Observable<List<Film>>

    // Метод для получения списка "Посмотреть позже" (синхронный для работы с Mediator)
    @Query("SELECT * FROM film_table WHERE isInWatchLater = 1")
    fun getWatchLaterFilmsSync(): List<Film>
}
