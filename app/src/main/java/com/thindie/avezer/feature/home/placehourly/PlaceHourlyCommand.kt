package com.thindie.avezer.feature.home.placehourly

import com.thindie.avezer.engine.Command
import com.thindie.avezer.feature.home.HomeFlow

internal sealed interface PlaceHourlyCommand : Command {
  data object Back : PlaceHourlyCommand
}

internal fun HomeFlow.exec(
  command: PlaceHourlyCommand,
  state: PlaceHourlyState,
): PlaceHourlyState =
  when (command) {
    is PlaceHourlyCommand.Back -> {
      back()
      state
    }
  }
