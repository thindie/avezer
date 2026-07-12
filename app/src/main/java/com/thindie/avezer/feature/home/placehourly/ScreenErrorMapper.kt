package com.thindie.avezer.feature.home.placehourly

import com.thindie.avezer.engine.ScreenScopeError

internal fun placeHourlyScreenErrorMapper(): (
  error: Throwable,
) -> ScreenScopeError {
  return { e ->
    ScreenScopeError(
      message = "An unknown error occurred.",
      actions = mapOf(ScreenScopeError.Actions.Common.ButtonMain to PlaceHourlyCommand.Back),
    )
  }
}
