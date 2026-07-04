package com.thindie.avezer.feature.search.input

import com.thindie.avezer.feature.search.SearchFlow
import kotlinx.coroutines.flow.firstOrNull

internal suspend fun SearchFlow.exec(
  command: SearchScreenCommand,
  state: SearchScreenState,
): SearchScreenState? =
  when (command) {
    is SearchScreenCommand.Search -> {
      state.copy(query = command.query)
    }

    is SearchScreenCommand.SelectCity -> {
      val selected = command.result
      val weather =
        flowModule.repository.read(
          lat = selected.lat,
          lon = selected.lon,
          cityName = selected.city,
        )
      finish(SearchFlow.Result.PlaceRequested(weather))
      null
    }

    is SearchScreenCommand.ToggleFavorite -> {
      flowModule.placesRepository.toggleFavorite(command.favoriteLocation)
      val favorites = flowModule.placesRepository.favoriteCities.firstOrNull().orEmpty()
      state.copy(favorites = favorites)
    }

    is SearchScreenCommand.OpenSearch -> {
      go(this@exec.SearchRoute)
      null
    }

    SearchScreenCommand.Back -> {
      back()
      null
    }

    SearchScreenCommand.ConfirmSearch -> {
      flowModule.placesRepository.search(state.query)
      null
    }
  }
