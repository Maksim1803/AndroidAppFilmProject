package com.example.database_module.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.database_module.dao.FilmDao
import com.example.database_module.entity.Film

@Database(entities = [Film::class], version = 2, exportSchema = false)
// Создаем класс AppDatabase, который представляет собой базу данных Room.
// Указываем, какие сущности (таблицы) есть в базе данных и ее версию.
abstract class AppDatabase : RoomDatabase() {

    // Метод для получения экземпляра FilmDao,
    // через который мы будем взаимодействовать с базой данных.
    abstract fun filmDao(): FilmDao
}
