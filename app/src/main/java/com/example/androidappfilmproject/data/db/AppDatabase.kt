package com.example.androidappfilmproject.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.androidappfilmproject.data.dao.FilmDao
import com.example.androidappfilmproject.data.entity.Film

// Создаем класс AppDatabase, который представляет собой базу данных Room.
// Указываем, какие сущности (таблицы) есть в базе данных и ее версию.
@Database(entities = [Film::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    // Метод для получения экземпляра FilmDao,
    // через который мы будем взаимодействовать с базой данных.
    abstract fun filmDao(): FilmDao
}
