package com.example.androidappfilmproject

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.androidappfilmproject.domain.Film

// Создаем класс AppDatabase, который представляет собой базу данных Room.
// Указываем, какие сущности (таблицы) есть в базе данных и ее версию.
@Database(entities = [Film::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    // Метод для получения экземпляра FilmDao, через который мы будем взаимодействовать с базой данных.
    abstract fun filmDao(): FilmDao
}
