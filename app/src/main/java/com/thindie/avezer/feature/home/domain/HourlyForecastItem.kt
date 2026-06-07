package com.thindie.avezer.feature.home.domain

import androidx.compose.runtime.Immutable

@Immutable
data class HourlyForecastItem(
  val time: String,
  val temperature: Double,
  val humidity: Int?,
  val windSpeed: Double,
  val precipitation: Double,
  val weatherCodeRef: Int,
  val emoji: String,
)
