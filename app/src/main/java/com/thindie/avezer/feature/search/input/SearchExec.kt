package com.thindie.avezer.feature.search.input

import com.thindie.avezer.feature.search.SearchFlow

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
      flowModule.searchRepository.toggleFavorite(command.favoriteLocation)
      null
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
      flowModule.searchRepository.search(state.query)
      null
    }
  }
