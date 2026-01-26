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

/*
Переведите всю асинхронную работу на Корутины (запросы в БД, получение данных для UI и т.д.)
⭐ Реализуйте с помощью операторов конвертацию ответа от API в DTO-объект (сейчас это выполняет класс Converter).

Подсказка 1
Нам нужно изменить тип возвращаемого значения в DAO методе, чтобы это сделать, нужна особая зависимость:
implementation "androidx.room:room-ktx:$room_version"
Подсказка 2
Далее по цепочке нужно изменить тип возвращаемого значения сначала в интеракоре, а потом и во View моделях.
Подсказка 3
На UI лучше все Корутины создавать в одном скоупе, так как их надо будет отменять, когда представление будет уничтожаться.
Подсказка 4
Для логики показа Прогресс-бара можно использовать Channel, отлично подойдет Conflated.

 */


