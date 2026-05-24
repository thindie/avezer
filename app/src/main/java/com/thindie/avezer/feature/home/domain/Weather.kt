package com.thindie.avezer.feature.home.domain

import com.thindie.avezer.feature.home.data.toDomainModel
import com.thindie.avezer.network.MockWeatherResponse
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

  val lastUpdated: Long = System.currentTimeMillis()
)

object MockWeather {
  fun create(): Weather = MockWeatherResponse.create().toDomainModel("Kaliningrad")!!
}
