package com.example.androidappfilmproject
import android.os.Parcel
import android.os.Parcelable

//Для первой части:
//data class Film(
//    val title: String,
//    val poster: Int,
//    val description: String
//)

// Для второй части:
@Film.Parcelize
data class Film(
    val title: String,
    val poster: Int,
    val description: String


) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readInt(),
        parcel.readString().toString()
    )

    annotation class Parcelize

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeInt(poster)
        parcel.writeString(description)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Film> {
        override fun createFromParcel(parcel: Parcel): Film {
            return Film(parcel)
        }

        override fun newArray(size: Int): Array<Film?> {
            return arrayOfNulls(size)
        }
    }
}



