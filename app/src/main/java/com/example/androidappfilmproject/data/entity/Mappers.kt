package com.example.androidappfilmproject.data.entity

import com.example.database_module.entity.Film
import com.example.remote_module.entity.TmdbFilm

// Расширение для конвертации TmdbFilm (из сетевого модуля) в Film (сущность БД)
fun TmdbFilm.toFilm(): Film {
    return Film(
        title = this.title,
        poster = this.posterPath,
        description = this.overview,
        rating = this.voteAverage,
        id = this.id,
        isInFavorites = false
    )
}
