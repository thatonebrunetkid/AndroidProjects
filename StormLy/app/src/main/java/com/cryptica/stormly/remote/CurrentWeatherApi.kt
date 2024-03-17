package com.cryptica.stormly.remote

import com.cryptica.stormly.data.models.CurrentWeatherModel
import com.cryptica.stormly.data.models.ForecastModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrentWeatherApi {
    @GET("weather")
    suspend fun getWeather(@Query("lat") lat: Double, @Query("lon") lon: Double, @Query("appid") apiKey: String, @Query("units") unit : String) : Response<CurrentWeatherModel>

    @GET("forecast")
    suspend fun getForecast(@Query("lat") lat: Double, @Query("lon") lon: Double, @Query("appid") apiKey: String, @Query("units") unit : String) : Response<ForecastModel>
}