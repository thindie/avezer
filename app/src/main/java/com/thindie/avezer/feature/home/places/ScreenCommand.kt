package com.thindie.avezer.feature.home.places

import com.thindie.avezer.engine.Command
import com.thindie.avezer.feature.home.HomeFlow
import com.thindie.avezer.feature.home.domain.MainRepository
import com.thindie.avezer.feature.home.domain.Weather
import com.thindie.avezer.feature.home.placehourly.placeHourly

internal sealed interface ScreenCommand : Command {
  data object Fetch : ScreenCommand

  data object Back : ScreenCommand

  data class SeeHourlyForecast(val weather: Weather) : ScreenCommand
}

internal suspend fun HomeFlow.exec(
  command: ScreenCommand,
  state: ScreenState,
  repository: MainRepository,
): ScreenState =
  when (command) {
    is ScreenCommand.Fetch -> {
      repository.fetch()
      state
    }

    is ScreenCommand.Back -> {
      back()
      state
    }

    is ScreenCommand.SeeHourlyForecast -> {
      go(placeHourly(command.weather))
      state
    }
  }
