package com.example.database_module.di

import android.content.Context
import androidx.room.Room
import com.example.database_module.dao.FilmDao
import com.example.database_module.db.AppDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {
    @Singleton
    @Provides
    fun provideAppDatabase(context: Context): AppDatabase = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "film_db"
    ).fallbackToDestructiveMigration()
        .build()

    @Singleton
    @Provides
    fun provideFilmDao(appDatabase: AppDatabase): FilmDao = appDatabase.filmDao()
}
