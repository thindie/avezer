package com.thindie.avezer.feature.home.placedetail

import com.thindie.avezer.engine.Command
import com.thindie.avezer.feature.home.HomeFlow
import com.thindie.avezer.feature.home.domain.Weather

internal sealed interface PlaceDetailCommand : Command {
  data object Refresh : PlaceDetailCommand

  data class Init(val weather: Weather) : PlaceDetailCommand

  data object Fetch : PlaceDetailCommand

  data object Back : PlaceDetailCommand
}

internal suspend fun HomeFlow.exec(
  command: PlaceDetailCommand,
  state: PlaceDetailState,
): PlaceDetailState? =
  when (command) {
    is PlaceDetailCommand.Refresh -> {
      flowModule.repository.fetch()
      null
    }

    is PlaceDetailCommand.Fetch -> {
      flowModule.repository.fetch()
      null
    }

    is PlaceDetailCommand.Back -> {
      back()
      null
    }

    is PlaceDetailCommand.Init -> {
      state.copy(
        title = command.weather.city,
        dailyForecast = command.weather.forecast,
      )
    }
  }
