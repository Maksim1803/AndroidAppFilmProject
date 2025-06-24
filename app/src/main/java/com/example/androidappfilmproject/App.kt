package com.example.androidappfilmproject

import android.app.Application
import androidx.room.Room

class App : Application() {
    lateinit var db: AppDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "film_database" // Имя базы данных
        ).build()
    }

    companion object {
        lateinit var instance: App
            private set
    }
}
