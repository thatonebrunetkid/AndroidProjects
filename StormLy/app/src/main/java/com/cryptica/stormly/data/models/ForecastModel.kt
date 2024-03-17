package com.cryptica.stormly.data.models


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ForecastModel(
    @Json(name = "city")
    val city: City,
    @Json(name = "cnt")
    val cod: String,
    @Json(name = "list")
    val list: List<CondList>
)