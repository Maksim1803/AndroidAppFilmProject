package com.example.androidappfilmproject

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.androidappfilmproject.domain.Film

// Класс предоставляющий доступ к базе данных
@Database(entities = [Film::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun filmDao(): FilmDao
}
