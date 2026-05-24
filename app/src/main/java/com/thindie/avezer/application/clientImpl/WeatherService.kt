package com.thindie.avezer.application.clientImpl

import com.thindie.avezer.network.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
  @GET("v1/forecast/")
  suspend fun getForecast(
    @Query("latitude") lat: Double,
    @Query("longitude") lon: Double,
    @Query("hourly") hourly: List<String>?,
    @Query("daily") daily: List<String>?,
    @Query("forecast_days") forecastDays: Int,
    @Query("temperature_unit") tempUnit: String,
  ): WeatherResponse
}
