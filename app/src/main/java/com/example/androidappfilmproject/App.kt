package com.example.androidappfilmproject

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import com.example.androidappfilmproject.di.AppComponent
import com.example.androidappfilmproject.di.DaggerAppComponent
import com.example.androidappfilmproject.view.notifications.NotificationConstants
import com.example.remote_module.DaggerRemoteComponent

// Основной класс приложения, который инициализирует Dagger.
class App : Application() {

    // Поле для хранения экземпляра AppComponent, который
    // является главным компонентом Dagger.
    lateinit var dagger: AppComponent

    //Добавим флаг для проверки показа кастомной композиции view только в начале
    var isPromoShown = false

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

        // Устанавливаем сохраненную тему
        val savedTheme = dagger.getInteractor().getTheme()
        AppCompatDelegate.setDefaultNightMode(savedTheme)

        // Создаем канал уведомлений
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Watch Later Channel"
            val descriptionText = "Films Search notification channel"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel(NotificationConstants.CHANNEL_ID, name, importance)
            mChannel.description = descriptionText
            // Регистрируем канал в системе
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
    }

    // Companion object для доступа к экземпляру приложения.
    companion object {
        // Поле для хранения единственного экземпляра класса App.
        lateinit var instance: App
            // Приватный сеттер, чтобы экземпляр нельзя было изменить извне.
            private set
    }
}
