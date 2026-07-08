package com.thindie.avezer.widget

import com.thindie.avezer.feature.home.domain.ForecastRepository
import com.thindie.avezer.feature.home.domain.SearchRepository
import com.thindie.avezer.widget.data.WeatherDataProvider
import com.thindie.avezer.widget.data.WeatherWidgetData
import kotlinx.coroutines.flow.firstOrNull

class WidgetDataProviderImpl(
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

    return WeatherWidgetData.Forecast(
      city = weather.city,
      temperature = weather.temperature,
      emoji = weather.emoji,
      isDay = weather.isDay,
    )
  }
}
