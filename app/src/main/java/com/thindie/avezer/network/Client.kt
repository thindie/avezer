package com.thindie.avezer.network

interface Client {
  suspend fun getForecast(
    lat: Double,
    lon: Double,
    hourly: List<String>? = listOf("temperature_2m", "relative_humidity_2m"),
    daily: List<String>? = null,
    forecastDays: Int = 7,
    tempUnit: String = "celsius",
  ): WeatherResponse
}
