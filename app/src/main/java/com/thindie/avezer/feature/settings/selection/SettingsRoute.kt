package com.thindie.avezer.feature.settings.selection

import com.thindie.avezer.HomeSection
import com.thindie.avezer.engine.RouteFactory
import com.thindie.avezer.feature.settings.SettingsFlow

val SettingsFlow.selection
  get() = RouteFactory.create(
      id = "settings",
      initialState = SettingsState(language = repository.language()),
      execute = { cmd, state -> exec(cmd, state) },
      errorMapper = { settingsScreenErrorMapper(it) },
      routeContent = { scope -> SettingsScreenContent(scope) },
      section = HomeSection.Settings,
    )
