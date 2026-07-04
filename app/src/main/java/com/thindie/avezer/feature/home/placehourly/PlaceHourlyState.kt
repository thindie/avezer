package com.thindie.avezer.feature.home.placehourly

import androidx.compose.runtime.Immutable
import com.thindie.avezer.engine.ScreenScope
import com.thindie.avezer.engine.ViewState
import com.thindie.avezer.engine.stateSink
import com.thindie.avezer.engine.sub
import com.thindie.avezer.engine.transition
import com.thindie.avezer.feature.home.domain.ForecastRepository
import com.thindie.avezer.feature.home.domain.HourlyForecastItem
import com.thindie.avezer.feature.home.domain.Weather
import kotlinx.coroutines.flow.filter

@Immutable
data class PlaceHourlyState(val hourlyForecast: List<HourlyForecastItem>? = null) : ViewState

internal fun ScreenScope<PlaceHourlyState, PlaceHourlyCommand>.subscriptions(
  repository: ForecastRepository,
  weather: Weather,
) {
  stateSink(this) { scope ->
    scope.sub(repository.forecast.filter { it?.any { it.city == weather.city } == true }).transition(
      block = { _, forecast ->
        // Weather contains hourlyForecast as List<HourlyForecast>
        // We need to map it to HourlyForecastItem which has additional fields like emoji and weatherCodeRef
        val result =
          forecast?.flatMap { weather ->
            weather.hourlyForecast.map { hf ->
              HourlyForecastItem(
                time = hf.time,
                temperature = hf.temperature,
                humidity = hf.humidity,
                windSpeed = hf.windSpeed,
                precipitation = hf.precipitation,
                weatherCodeRef = hf.weatherCodeRef,
                emoji = hf.emoji,
              )
            }
          } ?: emptyList()
        PlaceHourlyState(result)
      },
    )
  }
}
