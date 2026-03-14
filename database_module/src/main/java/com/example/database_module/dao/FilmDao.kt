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
interface FilmDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(films: List<Film>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(film: Film)

    @Update
    fun update(film: Film)

    @Delete
    fun delete(film: Film)

    @Query("DELETE FROM film_table WHERE category = :category AND isInFavorites = 0")
    fun deleteByCategory(category: String)

    @Query("DELETE FROM film_table")
    fun deleteAll()

    @Query("SELECT * FROM film_table")
    fun getAllFilms(): Observable<List<Film>>

    @Query("SELECT id FROM film_table WHERE isInFavorites = 1")
    fun getFavoriteIds(): List<Int>

    @Query("SELECT * FROM film_table WHERE category = :category")
    fun getFilmsPagingSource(category: String): PagingSource<Int, Film>

    @Query("SELECT * FROM film_table WHERE id = :id")
    fun getFilmById(id: Int): Observable<Film>

    @Query("SELECT * FROM film_table WHERE isInFavorites = 1")
    fun getFavoriteFilmsPagingSource(): PagingSource<Int, Film>

    // Метод для получения списка "Посмотреть позже"
    @Query("SELECT * FROM film_table WHERE isInWatchLater = 1 ORDER BY watchLaterTime ASC")
    fun getWatchLaterFilms(): Observable<List<Film>>
}
