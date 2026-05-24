package com.thindie.avezer.network

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

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
  val current: CurrentData? = null
)

@Serializable
data class HourlyData(
  val time: List<String?>? = emptyList(),
  @SerialName("temperature_2m") val temperature2m: List<Double?>? = emptyList(),
  @SerialName("relative_humidity_2m") val relativeHumidity2m: List<Int?>? = emptyList(),
  @SerialName("is_day") val isDay: Int? = null
)

@Serializable
data class DailyData(
  val time: List<String?>? = emptyList(),
  @SerialName("temperature_2m_max") val tempMax: List<Double?>? = emptyList(),
  @SerialName("temperature_2m_min") val tempMin: List<Double?>? = emptyList()
)

@Serializable
data class CurrentData(
  val time: String? = null,
  val temperature: Double? = null,
  @SerialName("wind_speed") val windSpeed: Double? = null,
  @SerialName("weather_code") val weatherCode: Int? = null
)
