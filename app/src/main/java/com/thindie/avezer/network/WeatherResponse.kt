package com.thindie.avezer.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponse(
  val latitude: Double? = null,
  val longitude: Double? = null,
  val elevation: Double? = null,
  @SerialName("utc_offset_seconds") val utcOffsetSeconds: Int? = null,
  val timezone: String? = null,
  @SerialName("timezone_abbreviation") val timezoneAbbreviation: String? = null,
  val hourly: HourlyData? = null,
  val daily: DailyData? = null,
  val current: CurrentData? = null,
)

@Serializable
data class HourlyData(
  val time: List<String?>? = emptyList(),
  @SerialName("temperature_2m") val temperature2m: List<Double?>? = emptyList(),
  @SerialName("relative_humidity_2m") val relativeHumidity2m: List<Int?>? = emptyList(),
  @SerialName("is_day") val isDay: Int? = null,
)

@Serializable
data class DailyData(
  val time: List<String?>? = emptyList(),
  @SerialName("temperature_2m_max") val tempMax: List<Double?>? = emptyList(),
  @SerialName("temperature_2m_min") val tempMin: List<Double?>? = emptyList(),
)

@Serializable
data class CurrentData(
  val time: String? = null,
  val temperature: Double? = null,
  @SerialName("wind_speed") val windSpeed: Double? = null,
  @SerialName("weather_code") val weatherCode: Int? = null,
)

object MockWeatherResponse {
  fun create(): WeatherResponse =
    WeatherResponse(
      latitude = 52.0,
      longitude = 53.0,
      elevation = 10.0,
      utcOffsetSeconds = 10800,
      timezone = "Europe/Kaliningrad",
      hourly =
        HourlyData(
          time = listOf("2024-06-15T12:00"),
          temperature2m = listOf(18.0),
          relativeHumidity2m = listOf(65),
          isDay = 1,
        ),
      daily =
        DailyData(
          time = listOf("2024-06-15"),
          tempMax = listOf(22.0),
          tempMin = listOf(14.0),
        ),
      current =
        CurrentData(
          time = "2024-06-15T12:00",
          temperature = 18.0,
          windSpeed = 3.5,
          weatherCode = 1000,
        ),
    )
}
