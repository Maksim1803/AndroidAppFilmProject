package com.example.remote_module.entity

import com.google.gson.annotations.SerializedName

data class TmdbTranslations(
    @SerializedName("id")
    val id: Int,
    @SerializedName("translations")
    val translations: List<Translation>
)

data class Translation(
    @SerializedName("iso_639_1")
    val language: String,
    @SerializedName("data")
    val data: TranslationData
)

data class TranslationData(
    @SerializedName("title")
    val title: String,
    @SerializedName("overview")
    val overview: String
)
