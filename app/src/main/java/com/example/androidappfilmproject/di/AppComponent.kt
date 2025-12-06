package com.example.androidappfilmproject.di

import android.content.Context
import com.example.androidappfilmproject.di.modules.DatabaseModule
import com.example.androidappfilmproject.di.modules.DomainModule
import com.example.androidappfilmproject.di.modules.RemoteModule
import com.example.androidappfilmproject.domain.FilmInteractor
import com.example.androidappfilmproject.viewmodel.HomeFragmentViewModel
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

// Аннотация @Singleton указывает, что он управляет жизненным циклом Singleton-зависимостей.
@Singleton
@Component(
    //Внедряем все модули, нужные для этого компонента
    modules = [
        RemoteModule::class,
        DatabaseModule::class,
        DomainModule::class
    ]
)
// Интерфейс главного Dagger-компонента приложения
interface AppComponent {

    // Метод внедряющий зависимости в HomeFragmentViewModel
    fun inject(homeFragmentViewModel: HomeFragmentViewModel)

    // Метод предоставляющий экземпляр FilmInteractor для доступа к нему через компонент
    fun filmInteractor(): FilmInteractor

    // Вложенный интерфейс для создания экземпляра компонента
    @Component.Factory
    interface Factory {
        // Метод создающий экземпляр компонента и привязывающий Context к графу зависимостей
        fun create(@BindsInstance context: Context): AppComponent
    }
}
