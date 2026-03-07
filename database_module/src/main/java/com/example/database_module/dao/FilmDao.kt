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

@Dao
// Создаем интерфейс FilmDao, который определяет методы для доступа к базе данных.
interface FilmDao {

    // Метод для вставки списка фильмов в базу данных.
    // При конфликте (например, одинаковый ID) старые фильмы заменяются новыми.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(films: List<Film>)

    // Метод для вставки одного фильма.
    // Если фильм уже есть в БД, он будет проигнорирован.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(film: Film)

    // Метод для обновления данных существующего фильма в базе данных.
    @Update
    fun update(film: Film)

    // Метод для удаления конкретного фильма из базы данных.
    @Delete
    fun delete(film: Film)

    // Метод для очистки кэша (фильмы конкретной категории), не удаляя избранное
    @Query("DELETE FROM film_table WHERE category = :category AND isInFavorites = 0")
    fun deleteByCategory(category: String)

    // Метод для полной очистки таблицы с фильмами.
    @Query("DELETE FROM film_table")
    fun deleteAll()

    // Метод для получения всех фильмов из базы данных в виде Observable.
    @Query("SELECT * FROM film_table")
    fun getAllFilms(): Observable<List<Film>>

    // Метод для получения списка ID избранных фильмов
    @Query("SELECT id FROM film_table WHERE isInFavorites = 1")
    fun getFavoriteIds(): List<Int>

    // Метод для получения всех фильмов для постраничной загрузки через Paging Library.
    @Query("SELECT * FROM film_table WHERE category = :category")
    fun getFilmsPagingSource(category: String): PagingSource<Int, Film>

    // Метод для получения одного фильма по его уникальному идентификатору (ID).
    @Query("SELECT * FROM film_table WHERE id = :id")
    fun getFilmById(id: Int): Observable<Film>

    // Метод для получения только избранных фильмов для постраничной загрузки.
    @Query("SELECT * FROM film_table WHERE isInFavorites = 1")
    fun getFavoriteFilmsPagingSource(): PagingSource<Int, Film>
}
