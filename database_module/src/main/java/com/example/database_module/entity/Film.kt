package com.example.database_module.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
// Создаем класс Film, который представляет собой модель данных фильма в приложении.
// Он использует алгоритм Parcelable для передачи между компонентами и аннотацию
// Entity для сохранения в базе данных Room.
@Entity(tableName = "film_table")
data class Film(
    // Название фильма
    val title: String,
    // Ссылка на постер фильма
    val poster: String?,
    // Описание фильма
    val description: String,
    // Рейтинг фильма
    var rating: Double = 0.0,
    // Флаг, указывающий, находится ли фильм в избранном
    var isInFavorites: Boolean = false,
    // Добавляем поле категории, чтобы различать фильмы в БД
    var category: String = "popular",
    // Поле для "Посмотреть позже"
    var isInWatchLater: Boolean = false,
    // Время напоминания
    var watchLaterTime: Long = 0L,
    // Уникальный идентификатор фильма, является первичным ключом в базе данных.
    @PrimaryKey
    val id: Int
) : Parcelable
