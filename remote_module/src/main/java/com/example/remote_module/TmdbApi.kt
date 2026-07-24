package com.example.remote_module

import com.example.remote_module.entity.TmdbResults
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

// Создаем интерфейс TmdbApi для взаимодействия с сервером
// The Movie Database API через Retrofit.
interface TmdbApi {

    // Метод для получения категорий фильмов.
    @GET("3/movie/{category}")
    fun getFilms(
        @Path("category") category: String, // Категория фильмов (popular, top_rated и т.д.)
        @Query("api_key") apiKey: String, // Ключ API для доступа к TM DB
        @Query("language") language: String, // Язык результатов
        @Query("page") page: Int, // Номер страницы для пагинации
        @Query("region") region: String? = null // Регион (для уточнения выдачи)
    ): Observable<TmdbResults>

    // Метод для поиска фильмов по запросу.
    @GET("3/search/movie")
    fun searchFilms(
        @Query("api_key") apiKey: String,
        @Query("language") language: String,
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("region") region: String? = null
    ): Observable<TmdbResults>

    // Метод для получения видео (трейлеров) фильма
    @GET("3/movie/{movie_id}/videos")
    fun getTrailers(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String? = null,
        @Query("include_video_language") includeVideoLanguage: String? = null
    ): Observable<com.example.remote_module.entity.TmdbVideoResults>

    // Метод для получения подробной информации о фильме
    @GET("3/movie/{movie_id}")
    fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String
    ): Observable<com.example.remote_module.entity.TmdbFilm>

    // Метод для получения всех доступных переводов фильма
    @GET("3/movie/{movie_id}/translations")
    fun getTranslations(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String
    ): Observable<com.example.remote_module.entity.TmdbTranslations>
}
