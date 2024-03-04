package com.cryptica.stormly.data.models


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Rain(
    @Json(name = "1h")
    val h: Double
)