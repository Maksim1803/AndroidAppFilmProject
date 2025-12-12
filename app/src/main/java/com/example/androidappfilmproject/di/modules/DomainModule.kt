package com.example.androidappfilmproject.di.modules

import com.example.androidappfilmproject.domain.FilmInteractor
import com.example.androidappfilmproject.domain.Interactor
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

// Абстрактный класс для Dagger-модуля, связывающего
// интерфейс Interactor с его реализацией.
@Module
abstract class DomainModule {

    // Аннотация @Singleton указывает, что зависимость должна быть
    // единственным экземпляром.
    @Singleton

    // Метод @Binds связывает абстрактный интерфейс FilmInteractor
    // с его конкретной реализацией (Interactor).
    @Binds
    abstract fun bindInteractor(interactor: Interactor): FilmInteractor
}
