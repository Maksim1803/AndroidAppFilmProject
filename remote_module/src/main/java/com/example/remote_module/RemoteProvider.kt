package com.example.remote_module

// Интерфейс, который будет провайдить наш Retrofit сервис другим модулям
// (например, модулю app)
interface RemoteProvider {
    fun provideRemote(): TmdbApi
}
