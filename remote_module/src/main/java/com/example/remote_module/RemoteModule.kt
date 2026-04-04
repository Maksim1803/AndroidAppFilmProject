package com.example.remote_module

import com.example.remote_module.entity.ApiConstants
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

// Модуль Dagger для предоставления зависимостей, связанных с сетью
// (Retrofit, OkHttp, TmdbApi).
@Module
class RemoteModule {
    @Provides
    @Singleton
    // Метод предоставляет экземпляр Gson для сериализации/десериализации JSON.
    fun provideGson(): Gson = GsonBuilder()
        .setLenient()
        .create()

    @Provides
    @Singleton
    // Метод предоставляет экземпляр HTTP-клиента OkHttpClient.
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        // Увеличиваем таймауты для стабильности при слабом соединении
        .callTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .connectTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            // Используем уровень BODY для детальной отладки сетевых запросов
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    @Provides
    @Singleton
    // Метод предоставляет экземпляр Retrofit (клиент для выполнения HTTP-запросов)
    fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit = Retrofit.Builder()
        .baseUrl(ApiConstants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .client(okHttpClient)
        .build()

    @Provides
    @Singleton
    // Метод предоставляет экземпляр API-интерфейса TmdbApi, используя Retrofit
    fun provideTmdbApi(retrofit: Retrofit): TmdbApi = retrofit.create(TmdbApi::class.java)
}
