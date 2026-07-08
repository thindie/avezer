package com.thindie.avezer.feature.settings.managefavorites

import com.thindie.avezer.engine.RouteFactory
import com.thindie.avezer.engine.ScreenScopeError
import com.thindie.avezer.engine.stateSink
import com.thindie.avezer.engine.sub
import com.thindie.avezer.engine.transition
import com.thindie.avezer.feature.settings.SettingsFlow

val SettingsFlow.manageFavorite
  get() =
    RouteFactory.create(
      id = "manage_favorites",
      initialState = ManageFavoritesState(),
      execute = { cmd, state -> exec(cmd, state) },
      stateSink = { scope ->
        stateSink(scope) {
          it.sub(searchRepository.favorites)
            .transition {
                state, locations ->
              state.copy(favorites = locations)
            }
        }
      },
      errorMapper = { e ->
        ScreenScopeError(
          message = "An unknown error occurred.",
          actions = mapOf(ScreenScopeError.Actions.Common.ButtonMain to ManageFavoritesCommand.Back),
        )
      },
      initialCommand = { ManageFavoritesCommand.Init as ManageFavoritesCommand },
      routeContent = { scope -> ManageFavoritesScreenContent(scope) },
    )
