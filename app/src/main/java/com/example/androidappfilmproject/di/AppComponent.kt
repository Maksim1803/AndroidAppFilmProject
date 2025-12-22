package com.example.androidappfilmproject.di

import android.content.Context
import com.example.androidappfilmproject.di.modules.DatabaseModule
import com.example.androidappfilmproject.di.modules.DomainModule
import com.example.androidappfilmproject.di.modules.RemoteModule
import com.example.androidappfilmproject.di.modules.ViewModelModule
import com.example.androidappfilmproject.view.fragments.DemoFragment
import com.example.androidappfilmproject.view.fragments.DetailsFragment
import com.example.androidappfilmproject.view.fragments.FavoritesFragment
import com.example.androidappfilmproject.view.fragments.HomeFragment
import com.example.androidappfilmproject.view.fragments.SelectionsFragment
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

// Главный компонент Dagger для всего приложения.
// Он связывает все модули и предоставляет методы для внедрения зависимостей.
@Singleton
@Component(
    modules = [
        RemoteModule::class,
        DatabaseModule::class,
        DomainModule::class,
        ViewModelModule::class
    ]
)
interface AppComponent {
    // Методы для внедрения зависимостей во фрагменты
    fun inject(homeFragment: HomeFragment)
    fun inject(selectionsFragment: SelectionsFragment)
    fun inject(demoFragment: DemoFragment)
    fun inject(detailsFragment: DetailsFragment)
    fun inject(favoritesFragment: FavoritesFragment) // Добавляем новый метод

    // Фабрика для создания экземпляра AppComponent.
    // Позволяет передать в граф зависимостей внешний экземпляр, в данном случае, Context.
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }
}
