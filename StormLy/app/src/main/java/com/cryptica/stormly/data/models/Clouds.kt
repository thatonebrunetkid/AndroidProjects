package com.cryptica.stormly.data.models


import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Clouds(
    @SerializedName("all")
    val all: Int
)