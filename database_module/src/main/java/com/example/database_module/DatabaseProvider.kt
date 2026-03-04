package com.example.database_module

import com.example.database_module.dao.FilmDao

// Интерфейс-мостик для предоставления доступа к БД другим модулям

interface DatabaseProvider {
    fun provideFilmDao(): FilmDao
}
