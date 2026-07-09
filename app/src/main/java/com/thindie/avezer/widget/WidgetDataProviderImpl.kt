package com.thindie.avezer.widget

import android.annotation.SuppressLint
import android.content.Context
import com.thindie.avezer.application.formatRelativeTimeShort
import com.thindie.avezer.feature.home.domain.ForecastRepository
import com.thindie.avezer.feature.home.domain.SearchRepository
import com.thindie.avezer.widget.data.WeatherDataProvider
import com.thindie.avezer.widget.data.WeatherWidgetData
import com.thindie.avezer.widget.data.WidgetInteraction
import kotlinx.coroutines.flow.firstOrNull
import java.lang.String.format
import java.time.LocalDate
import java.time.LocalDateTime

class WidgetDataProviderImpl(
  private val context: Context,
  private val forecastRepository: ForecastRepository,
  private val searchRepository: SearchRepository,
  override val interaction: WidgetInteraction,
) : WeatherDataProvider {
  override suspend fun fetch() {
    forecastRepository.fetch()
    searchRepository.fetchFavorites()
  }

  @SuppressLint("DefaultLocale")
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

    val windSpeedRef = com.thindie.avezer.R.string.weather_wind_speed_value

    val windSpeedStr =
      weather.windSpeed?.let {
        context.getString(windSpeedRef, it.toString())
      }

    val lastUpdatedStr = formatRelativeTimeShort(weather.lastUpdated, context)

    val remainingPrecipitation =
      weather.hourlyForecast
        .filter { LocalDateTime.parse(it.time).hour >= LocalDateTime.now().hour && it.precipitation > 0 }
        .sumOf { it.precipitation }

    val mmRef = com.thindie.avezer.R.string.millimeter
    val mmString = context.getString(mmRef)
    val precipitationSumStr =
      if (remainingPrecipitation > 0) "${format("%.1f", remainingPrecipitation)} $mmString" else null

    // Compute daily temperature range for bar chart and current day index
    val dailyTempsMax = weather.forecast.map { it.temperatureMax }
    val dailyTempsMin = weather.forecast.map { it.temperatureMin }
    val nowDate = LocalDate.now().dayOfMonth
    val currentDayIndex =
      weather.forecast.indexOfFirst {
        LocalDate.parse(it.time).dayOfMonth == nowDate
      }

    return WeatherWidgetData.Forecast(
      city = weather.city,
      temperature = weather.temperature,
      emoji = weather.emoji,
      isDay = weather.isDay,
      precipitationSum = precipitationSumStr,
      windSpeed = windSpeedStr,
      lastUpdated = lastUpdatedStr,
      dailyTempsMax = dailyTempsMax,
      dailyTempsMin = dailyTempsMin,
      currentDayIndex = currentDayIndex,
    )
  }
}
