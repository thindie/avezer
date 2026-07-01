package com.thindie.avezer.feature.home.search

import com.thindie.avezer.application.AppStrings
import com.thindie.avezer.engine.ScreenScopeError

typealias SearchScreenErrorMapper = (e: Throwable) -> ScreenScopeError

fun searchScreenErrorMapper(): SearchScreenErrorMapper {
  return { _ ->
    ScreenScopeError(
      message = AppStrings.errorUnexpected,
      actions = mapOf(ScreenScopeError.Actions.Common.ButtonMain to SearchScreenCommand.Back),
    )
  }
}
