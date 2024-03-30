package com.cryptica.stormly.data.models


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CondList(
    @Json(name = "main")
    val main: Main,
    @Json(name = "weather")
    val weather: List<Weather>,
    @Json(name = "dt_txt")
    val time: String
)