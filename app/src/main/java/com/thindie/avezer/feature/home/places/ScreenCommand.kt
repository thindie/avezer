package com.thindie.avezer.feature.home.places

import com.thindie.avezer.engine.Command
import com.thindie.avezer.feature.home.HomeFlow
import com.thindie.avezer.feature.home.domain.ForecastRepository
import com.thindie.avezer.feature.home.domain.Weather
import com.thindie.avezer.feature.home.placedetail.placeDetail
import com.thindie.avezer.feature.home.placehourly.placeHourly

internal sealed interface ScreenCommand : Command {
  data object Refresh : ScreenCommand

  data object Fetch : ScreenCommand

  data object Back : ScreenCommand

  data object OpenSearch : ScreenCommand

  data class SeeDailyForecast(val weather: Weather) : ScreenCommand

  data class SeeHourlyForecast(val weather: Weather) : ScreenCommand
}

internal suspend fun HomeFlow.exec(
  command: ScreenCommand,
  state: ScreenState,
  repository: ForecastRepository,
): ScreenState? =
  when (command) {
    is ScreenCommand.Refresh -> {
      repository.fetch()
      null
    }

    is ScreenCommand.Fetch -> {
      repository.fetch()
      null
    }

    is ScreenCommand.Back -> {
      back()
      null
    }

    is ScreenCommand.OpenSearch -> {
      finish(HomeFlow.Result.Search)
      null
    }

    is ScreenCommand.SeeDailyForecast -> {
      go(placeDetail(command.weather))
      null
    }

    is ScreenCommand.SeeHourlyForecast -> {
      go(placeHourly(command.weather))
      null
    }
  }
