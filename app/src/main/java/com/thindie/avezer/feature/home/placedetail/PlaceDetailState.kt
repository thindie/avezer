package com.thindie.avezer.feature.home.placedetail

import androidx.compose.runtime.Immutable
import com.thindie.avezer.engine.ScreenScope
import com.thindie.avezer.engine.ViewState
import com.thindie.avezer.engine.stateSink
import com.thindie.avezer.engine.sub
import com.thindie.avezer.engine.transition
import com.thindie.avezer.feature.home.domain.DailyForecast
import com.thindie.avezer.feature.home.domain.ForecastRepository
import com.thindie.avezer.feature.home.domain.HourlyForecastItem
import com.thindie.avezer.feature.home.domain.Weather
import kotlinx.coroutines.flow.filter

@Immutable
data class PlaceDetailState(
  val title: String? = null,
  val lastUpdated: Long? = null,
  val dailyForecast: List<DailyForecast>? = null,
  val hourlyForecast: List<HourlyForecastItem> = emptyList(),
  val weather: Weather? = null,
) : ViewState

internal fun ScreenScope<PlaceDetailState, PlaceDetailCommand>.subscriptions(
  repository: ForecastRepository,
  weather: Weather,
) {
  stateSink(this) { scope ->
    val cityFilter = weather.city
    scope.sub(
      repository.forecast.filter { list ->
        list != null && list.any { it.city == cityFilter }
      },
    ).transition(
      block = { _, forecast ->
        val dailyResult =
          forecast?.flatMap { w ->
            if (w.city == cityFilter) w.forecast else emptyList()
          } ?: emptyList()
        val hourlyResult =
          forecast?.flatMap { w ->
            if (w.city == cityFilter) {
              w.hourlyForecast.map {
                HourlyForecastItem(
                  time = it.time,
                  temperature = it.temperature,
                  humidity = it.humidity,
                  windSpeed = it.windSpeed,
                  precipitation = it.precipitation,
                  weatherCodeRef = it.weatherCodeRef,
                  emoji = it.emoji,
                )
              }
            } else {
              emptyList()
            }
          } ?: emptyList()
        PlaceDetailState(
          title = weather.city,
          lastUpdated = weather.lastUpdated,
          dailyForecast = dailyResult,
          hourlyForecast = hourlyResult,
          weather = weather,
        )
      },
    )
  }
}
