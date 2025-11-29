package com.example.androidappfilmproject.data

import com.example.androidappfilmproject.data.Entity.TmdbResults
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

// Создаем интерфейс TmdbApi для взаимодействия с сервером
// The Movie Database API через Retrofit.

interface TmdbApi {
    // Метод для получения списка популярных фильмов.
    @GET("3/movie/popular")
    suspend fun getFilms(
        @Query("api_key") apiKey: String, // Ключ API для доступа к TM DB
        @Query("language") language: String, // Язык результатов
        @Query("page") page: Int // Номер страницы для пагинации
    ): Response<TmdbResults> // Возвращает Response с результатами Tmdb

    // Метод для поиска фильмов по запросу.
    @GET("3/search/movie")
    suspend fun searchFilms(
        @Query("api_key") apiKey: String, // Ключ API для доступа к Tmdb
        @Query("language") language: String, // Язык результатов
        @Query("query") query: String, // Поисковый запрос
        @Query("page") page: Int // Номер страницы для пагинации
    ): Response<TmdbResults> // Возвращает Response с результатами Tmdb
}
