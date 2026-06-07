package com.thindie.avezer.network

interface Client {
  suspend fun getForecast(
    lat: Double,
    lon: Double,
    hourly: List<String>? = listOf("temperature_2m", "relative_humidity_2m"),
    daily: List<String>? = listOf("temperature_2m_max", "temperature_2m_min", "weather_code", "sunset", "sunrise", "precipitation_sum"),
    current: List<String>? = listOf("temperature_2m", "relative_humidity_2m", "is_day", "weather_code", "rain", "precipitation"),
    forecastDays: Int = 7,
    tempUnit: String = "celsius",
  ): WeatherResponse
}
