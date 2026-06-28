package com.thindie.avezer.feature.home.search

import com.thindie.avezer.feature.home.HomeFlow
import com.thindie.avezer.feature.home.placehourly.placeHourly

internal suspend fun HomeFlow.exec(
  command: SearchScreenCommand,
  state: SearchScreenState,
): SearchScreenState =
  when (command) {
    is SearchScreenCommand.Search -> {
      state.copy(query = command.query)
    }

    is SearchScreenCommand.ClearSearch -> {
      state.copy(query = "", results = emptyList())
    }

    is SearchScreenCommand.SelectCity -> {
      go(placeHourly(command.weather))
      state
    }

    is SearchScreenCommand.ToggleFavorite -> {
      flowModule.placesRepository.toggleFavorite(command.city)
      state
    }

    is SearchScreenCommand.OpenSearch -> {
      go(searchPlaces)
      state
    }

    SearchScreenCommand.Back -> {
      back()
      state
    }

    SearchScreenCommand.ConfirmSearch -> {
      flowModule.placesRepository.search(state.query)
      state
    }
  }
