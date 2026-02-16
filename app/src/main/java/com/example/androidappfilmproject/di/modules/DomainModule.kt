package com.example.androidappfilmproject.di.modules

import android.content.Context
import com.example.androidappfilmproject.data.MainRepository
import com.example.androidappfilmproject.data.preferences.PreferenceProvider
import com.example.androidappfilmproject.domain.Interactor
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

// Модуль Dagger для предоставления зависимостей
@Module
class DomainModule {

    // Предоставляет синглтон-экземпляр PreferenceProvider для работы с SharedPreferences.
    @Singleton
    @Provides
    fun providePreferences(context: Context): PreferenceProvider = PreferenceProvider(context)

    // Предоставляет синглтон-экземпляр Interactor для взаимодействия с данными.
    @Singleton
    @Provides
    fun provideInteractor(
        repository: MainRepository,
        preferenceProvider: PreferenceProvider
    ): Interactor = Interactor(repo = repository, preferences = preferenceProvider)
}
