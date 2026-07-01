package com.thindie.avezer.feature.home.places

import com.thindie.avezer.engine.Command
import com.thindie.avezer.feature.home.HomeFlow
import com.thindie.avezer.feature.home.domain.MainRepository
import com.thindie.avezer.feature.home.domain.Weather
import com.thindie.avezer.feature.home.placedetail.placeDetail
import com.thindie.avezer.feature.home.placehourly.placeHourly
import com.thindie.avezer.feature.home.search.searchPlaces

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
  repository: MainRepository,
): ScreenState =
  when (command) {
    is ScreenCommand.Refresh -> {
      repository.fetch()
      state
    }

    is ScreenCommand.Fetch -> {
      repository.fetch()
      state
    }

    is ScreenCommand.Back -> {
      back()
      state
    }

    is ScreenCommand.OpenSearch -> {
      go(searchPlaces)
      state
    }

    is ScreenCommand.SeeDailyForecast -> {
      go(placeDetail(command.weather))
      state
    }

    is ScreenCommand.SeeHourlyForecast -> {
      go(placeHourly(command.weather))
      state
    }
  }
