package com.example.androidappfilmproject.utils

import com.example.androidappfilmproject.data.entity.Film
import com.example.androidappfilmproject.data.entity.TmdbFilm

// Создаем object Converter для преобразования данных из одного формата в другой
object Converter {

    //Метод для преобразования списка фильмов из формата API (TmdbFilm)
    //в формат, используемый в приложении (Film).
        fun convertApiListToDtoList(list: List<TmdbFilm>?): List<Film> {
        val result = mutableListOf<Film>()
        list?.forEach {           
            result.add(Film(
                title = it.title,
                poster = it.posterPath,
                description = it.overview,
                rating = it.voteAverage,
                id = it.id,
                isInFavorites = false // По умолчанию фильм не в избранном
            ))
        }
        return result
    }
}
