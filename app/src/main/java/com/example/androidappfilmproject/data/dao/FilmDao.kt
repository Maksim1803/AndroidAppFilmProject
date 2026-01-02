package com.example.androidappfilmproject.data.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.androidappfilmproject.data.entity.Film
import kotlinx.coroutines.flow.Flow

@Dao
interface FilmDao {

    @Query("SELECT * FROM film_table")
    fun getAllFilms(): Flow<List<Film>>

    @Query("SELECT * FROM film_table")
    fun getFilmsPagingSource(): PagingSource<Int, Film>

    @Query("SELECT * FROM film_table WHERE isInFavorites = 1")
    fun getFavoriteFilmsPagingSource(): PagingSource<Int, Film>

    @Query("SELECT * FROM film_table WHERE id = :id")
    fun getFilmById(id: Int): Flow<Film>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(film: Film)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<Film>)

    @Delete
    suspend fun delete(film: Film)

    @Query("DELETE FROM film_table")
    suspend fun deleteAll()
}
