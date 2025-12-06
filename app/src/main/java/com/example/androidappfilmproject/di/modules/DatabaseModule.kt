package com.example.androidappfilmproject.di.modules

import android.content.Context
import androidx.room.Room
import com.example.androidappfilmproject.AppDatabase
import com.example.androidappfilmproject.data.MainRepository
import com.example.androidappfilmproject.data.TmdbApi
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

// Класс для модуля Dagger, предоставляющего зависимости базы данных и репозитория
@Module
class DatabaseModule {
    // Аннотация @Singleton указывает, что должен быть создан единственный экземпляр
    @Singleton
    // Метод предоставляет экземпляр базы данных Room
    @Provides
    fun provideAppDatabase(context: Context) = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "film_db" // Имя файла базы данных
    ).fallbackToDestructiveMigration() // Позволяет удалять и создавать заново при изменении схемы
        .build()

    // Аннотация @Singleton указывает, что должен быть создан единственный экземпляр
    @Singleton
    // Метод предоставляет Data Access Object (DAO) для работы с сущностью Film
    @Provides
    fun provideFilmDao(appDatabase: AppDatabase) = appDatabase.filmDao()

    // Метод предоставляет основной репозиторий приложения
    @Provides
    @Singleton
    fun provideRepository(appDatabase: AppDatabase, tmdbApi: TmdbApi) = MainRepository(appDatabase, tmdbApi)
}