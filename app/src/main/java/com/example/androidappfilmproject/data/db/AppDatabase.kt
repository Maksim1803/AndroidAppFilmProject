package com.example.androidappfilmproject.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.androidappfilmproject.data.dao.FilmDao
import com.example.androidappfilmproject.data.entity.Film

@Database(entities = [Film::class], version = 1, exportSchema = false)
    abstract class AppDatabase : RoomDatabase() {
        abstract fun filmDao(): FilmDao
    }
