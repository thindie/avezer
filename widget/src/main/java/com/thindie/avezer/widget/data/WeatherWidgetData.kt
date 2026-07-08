package com.thindie.avezer.widget.data

import androidx.compose.runtime.Immutable

/**
 * class representing weather information displayed on the widget.
 */
@Immutable
sealed interface WeatherWidgetData {
  data object None : WeatherWidgetData

  data class Forecast(
    val city: String,
    val temperature: Double,
    val emoji: String,
    val isDay: Boolean = true,
    val precipitationSum: String? = null,
    val windSpeed: String? = null,
    val lastUpdated: String,
  ) : WeatherWidgetData

  data object Error : WeatherWidgetData

  data object Outdated : WeatherWidgetData
}
