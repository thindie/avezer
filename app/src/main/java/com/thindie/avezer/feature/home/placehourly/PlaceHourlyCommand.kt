package com.thindie.avezer.feature.home.placehourly

import com.thindie.avezer.engine.Command
import com.thindie.avezer.feature.home.HomeFlow

internal sealed interface PlaceHourlyCommand : Command {
  data object Refresh : PlaceHourlyCommand

  data object Fetch : PlaceHourlyCommand

  data object Back : PlaceHourlyCommand
}

internal suspend fun HomeFlow.exec(
  command: PlaceHourlyCommand,
  state: PlaceHourlyState,
): PlaceHourlyState =
  when (command) {
    is PlaceHourlyCommand.Refresh -> {
      flowModule.repository.fetch()
      state
    }

    is PlaceHourlyCommand.Fetch -> {
      flowModule.repository.fetch()
      state
    }

    is PlaceHourlyCommand.Back -> {
      back()
      state
    }
  }
