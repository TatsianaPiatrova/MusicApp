package com.example.musicapp.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Track(
    var id:Int?,
    val title: String?,
    val artist: String?,
    val bitmapUri: String,
    val trackUri: String?,
    val duration: Long?
)

