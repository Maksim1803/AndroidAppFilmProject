package com.example.androidappfilmproject

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

// Создаем интерфейс FilmDao, который определяет методы для доступа к базе данных
// (например, добавление, удаление, обновление и получение данных).
// Он должен быть аннотирован @Dao.
@Dao
interface FilmDao {
    //Аннотации для определения операций с базой данных.
    @Insert(onConflict = OnConflictStrategy.REPLACE) //выделение
    suspend fun insert(film: Film)

    @Update // добавление
    suspend fun update(film: Film)

    @Delete // удаление
    suspend fun delete(film: Film)

    // Обновление и получение данных из базы данных
   @Query("SELECT * FROM film_table")
    fun getAllFilms(): Flow<List<Film>>

    @Query("SELECT * FROM film_table WHERE isInFavorites = 1")
    fun getFavoriteFilms(): Flow<List<Film>>

    @Query("SELECT * FROM film_table WHERE id = :id")
    fun getFilmById(id: Int): Flow<Film>

}