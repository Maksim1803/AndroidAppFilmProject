package com.example.androidappfilmproject.data.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "film_table")
data class Film(
    val title: String,
    val poster: String?,
    val description: String,
    var rating: Double = 0.0,
    var isInFavorites: Boolean = false,
    @PrimaryKey
    val id: Int
) : Parcelable
