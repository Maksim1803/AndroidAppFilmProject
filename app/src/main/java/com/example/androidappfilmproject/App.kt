package com.example.androidappfilmproject

import android.app.Application
import androidx.room.Room
import com.example.androidappfilmproject.data.ApiConstants
import com.example.androidappfilmproject.data.MainRepository
import com.example.androidappfilmproject.data.TmdbApi
import com.example.androidappfilmproject.domain.Interactor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

// Создаем класс App, который является точкой входа в приложение
// и отвечает за инициализацию основных компонентов.
class App : Application() {
    // Поле для хранения экземпляра базы данных
    lateinit var db: AppDatabase
    // Поле для хранения экземпляра репозитория
    private lateinit var repo: MainRepository
    // Поле для хранения экземпляра интерактора
    lateinit var interactor: Interactor
    // Поле для хранения экземпляра Retrofit сервиса
    private lateinit var retrofitService: TmdbApi

    // Метод, вызываемый при создании приложения
    override fun onCreate() {
        super.onCreate()
        // Сохраняем экземпляр класса App в статическом поле
        instance = this

        // Создаем пул потоков для работы с базой данных Room
        val dbExecutor = Executors.newSingleThreadExecutor()

        // Инициализируем базу данных Room
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "film_db"
        )
        .setQueryExecutor(dbExecutor) // Указываем исполнителя для запросов
        .setTransactionExecutor(dbExecutor) // Указываем исполнителя для транзакций
        .fallbackToDestructiveMigration(true) // При миграции базы данных, если не найден путь миграции, база данных будет пересоздана
        .build()

        // Создаем и настраиваем OkHttpClient для сетевых запросов
        val okHttpClient = OkHttpClient.Builder()
            .callTimeout(30, TimeUnit.SECONDS) // Устанавливаем таймаут для вызова
            .readTimeout(30, TimeUnit.SECONDS) // Устанавливаем таймаут для чтения
            .addInterceptor(HttpLoggingInterceptor().apply { // Добавляем интерцептор для логирования
                if (BuildConfig.DEBUG) {
                    level = HttpLoggingInterceptor.Level.BASIC
                }
            })
            .build()

        // Создаем и настраиваем Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl(ApiConstants.BASE_URL) // Устанавливаем базовый URL
            .addConverterFactory(GsonConverterFactory.create()) // Добавляем конвертер для Gson
            .client(okHttpClient) // Устанавливаем OkHttpClient
            .build()

        // Создаем экземпляр нашего Retrofit сервиса
        retrofitService = retrofit.create(TmdbApi::class.java)
        // Инициализируем репозиторий
        repo = MainRepository(db, retrofitService)
        // Инициализируем интерактор
        interactor = Interactor(repo, retrofitService)
    }

    // Создаем companion object для хранения статического экземпляра класса App
    companion object {
        lateinit var instance: App
            private set
    }
}
