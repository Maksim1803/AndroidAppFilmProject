package com.example.androidappfilmproject.data.entity

import com.google.gson.annotations.SerializedName

// Создаем класс, отвечающий за модель данных результатов, полученных от TmdbApi
data class TmdbResults(
    @SerializedName("page")
    val page: Int,
    @SerializedName("results")
    val tmdbFilms: List<TmdbFilm>,
    @SerializedName("total_pages")
    val totalPages: Int,
    @SerializedName("total_results")
    val totalResults: Int
)

