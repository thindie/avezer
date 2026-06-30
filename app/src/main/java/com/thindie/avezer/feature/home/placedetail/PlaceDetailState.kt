package com.thindie.avezer.feature.home.placedetail

import androidx.compose.runtime.Immutable
import com.thindie.avezer.engine.ScreenScope
import com.thindie.avezer.engine.ViewState
import com.thindie.avezer.engine.stateSink
import com.thindie.avezer.engine.sub
import com.thindie.avezer.engine.transition
import com.thindie.avezer.feature.home.domain.DailyForecast
import com.thindie.avezer.feature.home.domain.MainRepository
import com.thindie.avezer.feature.home.domain.Weather
import kotlinx.coroutines.flow.filter

@Immutable
data class PlaceDetailState(
  val title: String? = null,
  val dailyForecast: List<DailyForecast>? = null,
) : ViewState

internal fun ScreenScope<PlaceDetailState, PlaceDetailCommand>.subscriptions(
  repository: MainRepository,
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
        val result =
          forecast?.flatMap { w ->
            if (w.city == cityFilter) w.forecast else emptyList()
          } ?: emptyList()
        PlaceDetailState(title = weather.city, dailyForecast = result)
      },
    )
  }
}
