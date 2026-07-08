package com.thindie.avezer.widget

import android.content.Context
import com.thindie.avezer.application.formatRelativeTimeShort
import com.thindie.avezer.feature.home.domain.ForecastRepository
import com.thindie.avezer.feature.home.domain.SearchRepository
import com.thindie.avezer.widget.data.WeatherDataProvider
import com.thindie.avezer.widget.data.WeatherWidgetData
import kotlinx.coroutines.flow.firstOrNull

class WidgetDataProviderImpl(
  private val context: Context,
  private val forecastRepository: ForecastRepository,
  private val searchRepository: SearchRepository,
) : WeatherDataProvider {
  override suspend fun fetch() {
    forecastRepository.fetch()
  }

  override suspend fun getWidgetData(): WeatherWidgetData {
    val forecasts = forecastRepository.forecast.firstOrNull() ?: return WeatherWidgetData.None
    val favoriteLocation = searchRepository.favorites.firstOrNull()?.firstOrNull { it.usedForWidget }
    if (forecasts.isEmpty()) return WeatherWidgetData.None

    val weather =
      if (favoriteLocation != null) {
        forecasts.firstOrNull { it.city == favoriteLocation.city }
      } else {
        val weather = forecasts.first()
        weather
      } ?: return WeatherWidgetData.None

    val windSpeedStr = weather.windSpeed?.let { "%.1f km/h".format(it) }

    val lastUpdatedStr = formatRelativeTimeShort(weather.lastUpdated, context)

    val nowHour = java.time.LocalTime.now()
    val remainingPrecipitation =
      weather.hourlyForecast
        .filter { it.time.startsWith("T") && java.time.LocalTime.parse(it.time.substringAfter("T")) >= nowHour }
        .sumOf { it.precipitation }
    val precipitationSumStr =
      if (remainingPrecipitation > 0) "%.1f mm".format(remainingPrecipitation) else null

    return WeatherWidgetData.Forecast(
      city = weather.city,
      temperature = weather.temperature,
      emoji = weather.emoji,
      isDay = weather.isDay,
      precipitationSum = precipitationSumStr,
      windSpeed = windSpeedStr,
      lastUpdated = lastUpdatedStr,
    )
  }
}
