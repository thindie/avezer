package com.thindie.avezer.feature.settings.selection

import com.thindie.avezer.HomeSection
import com.thindie.avezer.engine.RouteFactory
import com.thindie.avezer.engine.stateSink
import com.thindie.avezer.engine.sub
import com.thindie.avezer.engine.transition
import com.thindie.avezer.feature.settings.SettingsFlow

val SettingsFlow.selection
  get() =
    RouteFactory.create(
      id = "settings",
      initialState = SettingsState(language = repository.language()),
      execute = { cmd, state -> exec(cmd, state) },
      stateSink = { scope ->
        stateSink(scope) {
          it.sub(searchRepository.favorites)
            .transition { state, locations -> state.copy(savedLocations = locations) }
        }
      },
      initialCommand = { SettingsCommand.Init as SettingsCommand },
      errorMapper = { settingsScreenErrorMapper(it) },
      routeContent = { scope -> SettingsScreenContent(scope) },
      section = HomeSection.Settings,
    )
