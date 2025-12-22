package com.example.androidappfilmproject.data

import com.example.androidappfilmproject.data.Entity.TmdbResults
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApi {
    // Убрана опечатка в аннотации GET
    @GET("3/movie/{category}")
    suspend fun getFilms(
        @Path("category") category: String,
        @Query("api_key") apiKey: String,
        @Query("language") language: String,
        @Query("page") page: Int
    ): Response<TmdbResults>

    @GET("3/search/movie")
    suspend fun searchFilms(
        @Query("api_key") apiKey: String,
        @Query("language") language: String,
        @Query("query") query: String,
        @Query("page") page: Int
    ): Response<TmdbResults>
}
