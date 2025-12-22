package com.example.androidappfilmproject

import android.app.Application
import com.example.androidappfilmproject.di.AppComponent
import com.example.androidappfilmproject.di.DaggerAppComponent

class App : Application() {
    lateinit var dagger: AppComponent

    override fun onCreate() {
        super.onCreate()
        instance = this
        // Создаем Dagger-компонент с помощью фабрики
        dagger = DaggerAppComponent.factory().create(this)
    }

    companion object {
        // Этот статический экземпляр нужен, чтобы фрагменты могли получить доступ к Dagger-компоненту
        lateinit var instance: App
            private set
    }
}
