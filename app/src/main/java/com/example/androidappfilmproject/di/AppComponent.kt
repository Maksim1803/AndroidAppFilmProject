package com.example.androidappfilmproject.di

import android.content.Context
import com.example.androidappfilmproject.di.modules.DatabaseModule
import com.example.androidappfilmproject.di.modules.DomainModule
import com.example.androidappfilmproject.di.modules.ViewModelModule
import com.example.androidappfilmproject.domain.Interactor
import com.example.androidappfilmproject.view.fragments.DemoFragment
import com.example.androidappfilmproject.view.fragments.DetailsFragment
import com.example.androidappfilmproject.view.fragments.FavoritesFragment
import com.example.androidappfilmproject.view.fragments.HomeFragment
import com.example.androidappfilmproject.view.fragments.SelectionsFragment
import com.example.androidappfilmproject.view.fragments.WatchLaterFragment
import com.example.remote_module.RemoteProvider
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

// Главный компонент Dagger для всего приложения.
// Теперь он зависит от RemoteProvider, который живет в другом модуле.

@Singleton
@Component(
    // Внедряем модули базы данных и домена.
    // RemoteModule теперь предоставляется через зависимости компонента (dependencies).
    dependencies = [RemoteProvider::class],
    modules = [
        DatabaseModule::class,
        DomainModule::class,
        ViewModelModule::class
    ]
)

//Интерфейс AppComponent определяющий методы для внедрения зависимостей во фрагменты.
interface AppComponent {
    // Методы для внедрения зависимостей во фрагменты
    fun inject(homeFragment: HomeFragment)
    fun inject(selectionsFragment: SelectionsFragment)
    fun inject(demoFragment: DemoFragment)
    fun inject(detailsFragment: DetailsFragment)
    fun inject(favoritesFragment: FavoritesFragment)
    fun inject(watchLaterFragment: WatchLaterFragment)

    // Добавляем метод для получения интерактора, чтобы использовать его в NotificationHelper
    fun getInteractor(): Interactor

    // Используем Builder для сборки компонента с внешними зависимостями
    @Component.Builder
    // Интерфейс для билдера компонента
    interface Builder {
        @BindsInstance
        // Метод для установки контекста в билдер
        fun context(context: Context): Builder
        // Метод для установки внешнего провайдера зависимостей
        fun remoteProvider(remoteProvider: RemoteProvider): Builder
        // Метод для сборки компонента
        fun build(): AppComponent
    }
}
