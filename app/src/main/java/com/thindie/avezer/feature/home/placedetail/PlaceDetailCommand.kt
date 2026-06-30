package com.thindie.avezer.feature.home.placedetail

import com.thindie.avezer.engine.Command
import com.thindie.avezer.feature.home.HomeFlow

internal sealed interface PlaceDetailCommand : Command {
  data object Back : PlaceDetailCommand
}

internal fun HomeFlow.exec(
  command: PlaceDetailCommand,
  state: PlaceDetailState,
): PlaceDetailState =
  when (command) {
    is PlaceDetailCommand.Back -> {
      back()
      state
    }
  }
