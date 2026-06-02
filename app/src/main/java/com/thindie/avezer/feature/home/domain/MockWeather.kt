package com.thindie.avezer.feature.home.domain

import androidx.compose.runtime.Immutable

@Immutable
data class MockWeather(
  val city: String,
  val temperature: Double,
  val weatherCodeRef: Int,
  val emoji: String,
) {
  companion object {
    fun create(): Weather =
      Weather(
        lat = 0.0,
        lon = 0.0,
        city = "Mock City",
        temperature = 20.0,
        isDay = true,
        weatherCodeRef = 1000,
        emoji = "☀️",
      )
  }
}
