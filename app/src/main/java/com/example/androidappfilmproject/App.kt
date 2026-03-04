package com.example.androidappfilmproject

import android.app.Application
import com.example.androidappfilmproject.di.AppComponent
import com.example.androidappfilmproject.di.DaggerAppComponent
import com.example.remote_module.DaggerRemoteComponent

// Основной класс приложения, который инициализирует Dagger.
class App : Application() {

    // Поле для хранения экземпляра AppComponent, который
    // является главным компонентом Dagger.
    lateinit var dagger: AppComponent

    // Метод, который вызывается при создании приложения.
    override fun onCreate() {
        super.onCreate()
        instance = this

        // 1. Сначала создаем компонент для сетевого модуля (remote_module)
        val remoteProvider = DaggerRemoteComponent.create()

        // 2. Затем создаем основной компонент приложения, 
        // передавая ему remoteProvider как зависимость.
        dagger = DaggerAppComponent.builder()
            .context(this)
            .remoteProvider(remoteProvider)
            .build()
    }

    // Companion object для доступа к экземпляру приложения.
    companion object {
        // Поле для хранения единственного экземпляра класса App.
        lateinit var instance: App
            // Приватный сеттер, чтобы экземпляр нельзя было изменить извне.
            private set
    }
}
