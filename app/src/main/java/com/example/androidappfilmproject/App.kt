package com.example.androidappfilmproject

import android.app.Application
import com.example.androidappfilmproject.di.DI
import com.example.androidappfilmproject.domain.Interactor
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

// Создаем класс App, который является точкой входа в приложение
// и отвечает за инициализацию основных компонентов.
class App : Application() {
    // Получаем Interactor из графа зависимостей Koin
    // Koin сам создаст его, когда он понадобится.
    val interactor: Interactor by inject()

    override fun onCreate() {
        super.onCreate()
        // Присваиваем статической переменной экземпляр этого
        // класса (самого себя)
        instance = this

        // Инициализация Koin
        startKoin {
            //Прикрепляем контекст
            androidContext(this@App)
            //(Опционально) подключаем зависимость
            androidLogger()
            //Инициализируем модули
            modules(listOf(DI.mainModule))
        }
    }
        // Companion object для хранения статических свойств
        companion object {
            // Статическая переменная для хранения единственного экземпляра класса App
            lateinit var instance: App
            // Сеттер приватный, чтобы присвоить значение можно было только
            // внутри этого класса
                private set
        }
    }

