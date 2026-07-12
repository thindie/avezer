package com.thindie.avezer.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponse(
  val latitude: Double,
  val longitude: Double,
  val elevation: Double,
  @SerialName("utc_offset_seconds") val utc_offset_seconds: Int,
  val timezone: String,
  @SerialName("timezone_abbreviation") val timezone_abbreviation: String,
  val hourly: HourlyData,
  val daily: DailyData,
  val current: CurrentData,
)

@Serializable
data class HourlyData(
  val time: List<String>,
  @SerialName("temperature_2m") val temperature_2m: List<Double>,
  @SerialName("relative_humidity_2m") val relative_humidity_2m: List<Int>,
  @SerialName("wind_speed_10m") val wind_speed_10m: List<Double>,
  @SerialName("precipitation") val precipitation: List<Double>,
  @SerialName("weather_code") val weather_code: List<Int>,
)

@Serializable
data class DailyData(
  val time: List<String>,
  @SerialName("temperature_2m_max") val temperature_2m_max: List<Double>,
  @SerialName("temperature_2m_min") val temperature_2m_min: List<Double>,
  @SerialName("weather_code") val weather_code: List<Int>,
  @SerialName("sunset") val sunset: List<String>,
  @SerialName("sunrise") val sunrise: List<String>,
  @SerialName("precipitation_sum") val precipitation_sum: List<Double>,
)

@Serializable
data class CurrentData(
  val temperature_2m: Double,
  val relative_humidity_2m: Double,
  val time: String,
  @SerialName("wind_speed_10m") val wind_speed_10m: Double,
  @SerialName("weather_code") val weather_code: Int,
  @SerialName("is_day") val is_day: Int,
  @SerialName("rain") val rain: Double,
  @SerialName("precipitation") val precipitation: Double,
)
