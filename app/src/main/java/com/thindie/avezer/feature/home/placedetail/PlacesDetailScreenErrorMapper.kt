package com.thindie.avezer.feature.home.placedetail

import com.thindie.avezer.engine.ScreenScopeError

internal fun placeDetailScreenErrorMapper(): (
  error: Throwable,
) -> ScreenScopeError {
  return { _ ->
    ScreenScopeError(
      message = "An unknown error occurred.",
      actions = mapOf(ScreenScopeError.Actions.Common.ButtonMain to PlaceDetailCommand.Back),
    )
  }
}
