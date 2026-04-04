package com.example.database_module.di

import android.content.Context
import androidx.room.Room
import com.example.database_module.dao.FilmDao
import com.example.database_module.db.AppDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

// Класс для модуля Dagger, предоставляющего зависимости базы данных и репозитория
@Module
class DatabaseModule {

    // Метод предоставляет экземпляр базы данных Room
    @Singleton
    @Provides
    fun provideAppDatabase(context: Context): AppDatabase = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "film_db"
    ).fallbackToDestructiveMigration()
        .build()

    // Метод предоставляет Data Access Object (DAO) для работы с сущностью Film
    @Singleton
    @Provides
    fun provideFilmDao(appDatabase: AppDatabase): FilmDao = appDatabase.filmDao()
}
