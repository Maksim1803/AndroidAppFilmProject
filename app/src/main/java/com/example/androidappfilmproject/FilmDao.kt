package com.example.androidappfilmproject

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.androidappfilmproject.domain.Film
import kotlinx.coroutines.flow.Flow

// Создаем интерфейс FilmDao, который определяет методы для доступа к базе данных
@Dao
interface FilmDao {
    // Метод для вставки одного фильма в базу данных. При конфликте старый фильм заменяется новым.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(film: Film)

    // Метод для вставки списка фильмов в базу данных. При конфликте старые фильмы заменяются новыми.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(films: List<Film>)

    // Метод для обновления фильма в базе данных.
    @Update
    suspend fun update(film: Film)

    // Метод для удаления фильма из базы данных.
    @Delete
    suspend fun delete(film: Film)

    // Метод для удаления всех фильмов из таблицы.
    @Query("DELETE FROM film_table")
    suspend fun deleteAll()

    // Метод для получения всех фильмов из базы данных в виде потока данных.
   @Query("SELECT * FROM film_table")
    fun getAllFilms(): Flow<List<Film>>

    // Метод для получения всех фильмов из базы данных для постраничной загрузки.
    @Query("SELECT * FROM film_table")
    fun getFilmsPagingSource(): PagingSource<Int, Film>

    // Метод для получения всех избранных фильмов.
    @Query("SELECT * FROM film_table WHERE isInFavorites = 1")
    fun getFavoriteFilms(): Flow<List<Film>>

    // Метод для получения фильма по его ID.
    @Query("SELECT * FROM film_table WHERE id = :id")
    fun getFilmById(id: Int): Flow<Film>

    // Метод для получения избранных фильмов для постраничной загрузки.
    @Query("SELECT * FROM film_table WHERE isInFavorites = 1")
    fun getFavoriteFilmsPagingSource(): PagingSource<Int, Film>

}
