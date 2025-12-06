package com.example.androidappfilmproject.di.modules

import com.example.androidappfilmproject.BuildConfig
import com.example.androidappfilmproject.data.ApiConstants
import com.example.androidappfilmproject.data.TmdbApi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
// Класс для Dagger-модуля, предоставляющего зависимости
// для работы с сетью (Retrofit, OkHttpClient).
class RemoteModule {


    @Provides
    @Singleton
    // Метод предоставляет экземпляр HTTP-клиента OkHttpClient.
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        //Настраиваем таймауты для медленного интернета
        .callTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        //Добавляем логгер
        .addInterceptor(HttpLoggingInterceptor().apply {
            if (BuildConfig.DEBUG) {
                level = HttpLoggingInterceptor.Level.BASIC
            }
        })
        .build()

    @Provides
    @Singleton
    // Метод предоставляет экземпляр Retrofit (клиент для выполнения HTTP-запросов)
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        //Указываем базовый URL из констант
        .baseUrl(ApiConstants.BASE_URL)
        //Добавляем конвертер
        .addConverterFactory(GsonConverterFactory.create())
        //Добавляем кастомный клиент
        .client(okHttpClient)
        .build()

    @Provides
    @Singleton
    // Метод предоставляет экземпляр API-интерфейса TmdbApi, используя Retrofit
    fun provideTmdbApi(retrofit: Retrofit): TmdbApi = retrofit.create(TmdbApi::class.java)
}
