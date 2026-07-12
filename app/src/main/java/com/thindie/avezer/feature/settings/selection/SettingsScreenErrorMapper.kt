package com.thindie.avezer.feature.settings.selection

import com.thindie.avezer.engine.ScreenScopeError

internal val settingsScreenErrorMapper: (Throwable) -> ScreenScopeError =
  { e ->
    ScreenScopeError(
      message = "An unknown error occurred.",
      actions = mapOf(ScreenScopeError.Actions.Common.ButtonMain to SettingsCommand.Back),
    )
  }
