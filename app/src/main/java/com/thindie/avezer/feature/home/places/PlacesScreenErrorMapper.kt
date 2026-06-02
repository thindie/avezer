package com.thindie.avezer.feature.home.places

import com.thindie.avezer.engine.ScreenScopeError

internal fun placesScreenErrorMapper(): (
    error: Throwable,
) -> ScreenScopeError {
    return { e ->
        ScreenScopeError(
            message = "An unknown error occurred.",
            actions = mapOf(ScreenScopeError.Actions.Common.ButtonMain to ScreenCommand.Fetch),
        )
    }
}
