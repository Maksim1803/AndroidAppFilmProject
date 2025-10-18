package com.example.androidappfilmproject

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

//Отредактировалось как надо, начиная с модуля 25
@Parcelize
// Создание базы данных избранных фильмов для модуля 26
@Entity(tableName = "film_table") // Имя таблицы

data class Film( // Класс представляет таблицу в базе данных

    val title: String,
    val poster: Int,
    val description: String,
    var rating: Float = 0f,
    var isInFavorites: Boolean = false,
    // Поле id с аннотацией @PrimaryKey необходимо для
    // уникальной идентификации каждой записи в базе данных
    @PrimaryKey(autoGenerate = true)//
    val id: Int = 0,

) : Parcelable
