package com.example.androidappfilmproject

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

//Для первой части:
//data class Film(
//    val title: String,
//    val poster: Int,
//    val description: String
//)

// Для второй части:
//@Film.Parcelize
//data class Film(
//    val title: String,
//    val poster: Int,
//    val description: String
//
//
//) : Parcelable {
//    constructor(parcel: Parcel) : this(
//        parcel.readString().toString(),
//        parcel.readInt(),
//        parcel.readString().toString()
//    )
//
//    annotation class Parcelize
//
//    override fun writeToParcel(parcel: Parcel, flags: Int) {
//        parcel.writeString(title)
//        parcel.writeInt(poster)
//        parcel.writeString(description)
//    }
//
//    override fun describeContents(): Int {
//        return 0
//    }
//
//    companion object CREATOR : Parcelable.Creator<Film> {
//        override fun createFromParcel(parcel: Parcel): Film {
//            return Film(parcel)
//        }
//
//        override fun newArray(size: Int): Array<Film?> {
//            return arrayOfNulls(size)
//        }
//    }
//}

//Отредактировалось как надо, начиная с модуля 25
@Parcelize
// Создание базы данных избранных фильмов для модуля 26
@Entity(tableName = "film_table") // Имя таблицы
data class Film( // Класс представляет таблицу в базе данных
    @PrimaryKey(autoGenerate = true)
    // Поле id с аннотацией @PrimaryKey необходимо для
    // уникальной идентификации каждой записи в базе данных

    val title: String,
    val poster: Int,
    val description: String,
    var isInFavorites: Boolean = false,
    val id: Int = 0,
) : Parcelable
