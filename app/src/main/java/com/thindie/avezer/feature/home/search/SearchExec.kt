package com.thindie.avezer.feature.home.search

import com.thindie.avezer.feature.home.HomeFlow
import com.thindie.avezer.feature.home.placehourly.placeHourly
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull

internal suspend fun HomeFlow.exec(
  command: SearchScreenCommand,
  state: SearchScreenState,
): SearchScreenState =
  when (command) {
    is SearchScreenCommand.Search -> {
      state.copy(query = command.query)
    }

    is SearchScreenCommand.SelectCity -> {
      val selected = command.result
      flowModule.repository.read(
        lat = selected.lat,
        lon = selected.lon,
        cityName = selected.city,
      )
      val result =
        flowModule.repository.forecast.first()?.firstOrNull {
          it.lon == selected.lon && it.lat == selected.lat
        }
      if (result != null) {
        go(placeHourly(result))
      }

      state
    }

    is SearchScreenCommand.ToggleFavorite -> {
      flowModule.placesRepository.toggleFavorite(command.city)
      val favorites = flowModule.placesRepository.favoriteCities.firstOrNull().orEmpty()
      state.copy(favorites = favorites)
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
