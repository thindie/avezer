package com.thindie.avezer.feature.home.domain

import androidx.compose.runtime.Immutable

@Immutable
data class Weather(
  val lat: Double,
  val lon: Double,
  val city: String,
  val temperature: Double,
  val isDay: Boolean = true,
  val weatherCodeRef: Int,
  val emoji: String,
  val humidity: Int? = null,
  val windSpeed: Double? = null,
  val lastUpdated: Long = System.currentTimeMillis(),
  val timezone: String,
  val timezoneAbbreviation: String,
  val utcOffsetSeconds: Int,
  val forecast: List<DailyForecast> = emptyList(),
  val hourlyForecast: List<HourlyForecast> = emptyList(),
)

@Immutable
data class DailyForecast(
  val time: String,
  val temperatureMax: Double,
  val temperatureMin: Double,
  val weatherCodeRef: Int,
  val emoji: String,
  val sunrise: String? = null,
  val sunset: String? = null,
  val precipitationSum: Double = 0.0,
)

@Immutable
data class HourlyForecast(
  val time: String,
  val temperature: Double,
  val humidity: Int?,
  val windSpeed: Double,
  val precipitation: Double,
  val weatherCodeRef: Int,
  val emoji: String,
)
