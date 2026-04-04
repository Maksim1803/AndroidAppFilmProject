package com.example.remote_module

// Интерфейс, который будет провайдить наш Retrofit сервис другим модулям
// (например, модулю app)
interface RemoteProvider {
    // Метод для получения экземпляра TmdbApi, который будет использоваться
    // для выполнения запросов к API
    fun provideRemote(): TmdbApi
}
