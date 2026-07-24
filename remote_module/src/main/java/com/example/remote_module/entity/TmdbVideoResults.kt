package com.example.remote_module.entity

import com.google.gson.annotations.SerializedName

data class TmdbVideoResults(
    @SerializedName("id")
    val id: Int,
    @SerializedName("results")
    val results: List<TmdbVideo>
)

data class TmdbVideo(
    @SerializedName("key")
    val key: String,
    @SerializedName("site")
    val site: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("iso_639_1")
    val language: String?
)
