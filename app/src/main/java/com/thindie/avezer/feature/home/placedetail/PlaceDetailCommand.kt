package com.thindie.avezer.feature.home.placedetail

import com.thindie.avezer.engine.Command
import com.thindie.avezer.feature.home.HomeFlow

internal sealed interface PlaceDetailCommand : Command {
  data object Refresh : PlaceDetailCommand

  data object Fetch : PlaceDetailCommand

  data object Back : PlaceDetailCommand
}

internal suspend fun HomeFlow.exec(
  command: PlaceDetailCommand,
  state: PlaceDetailState,
): PlaceDetailState =
  when (command) {
    is PlaceDetailCommand.Refresh -> {
      flowModule.repository.fetch()
      state
    }

    is PlaceDetailCommand.Fetch -> {
      flowModule.repository.fetch()
      state
    }

    is PlaceDetailCommand.Back -> {
      back()
      state
    }
  }
