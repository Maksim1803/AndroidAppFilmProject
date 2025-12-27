package com.example.androidappfilmproject

import android.app.Application
import com.example.androidappfilmproject.di.AppComponent
import com.example.androidappfilmproject.di.DaggerAppComponent

// Основной класс приложения, который инициализирует Dagger.
class App : Application() {

    // Поле для хранения экземпляра AppComponent, который
    // является главным компонентом Dagger.
    lateinit var dagger: AppComponent

    // Метод, который вызывается при создании приложения.
    override fun onCreate() {
        super.onCreate()
        instance = this
        // Создаем Dagger-компонент с помощью фабрики
        dagger = DaggerAppComponent.factory().create(this)
    }

    // Companion object для доступа к экземпляру приложения.
    companion object {
        // Поле для хранения единственного экземпляра класса App.
        lateinit var instance: App
            // Приватный сеттер, чтобы экземпляр нельзя было изменить извне.
            private set
    }
}
