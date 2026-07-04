package com.thindie.avezer.feature.search

import com.thindie.avezer.engine.ScreenScope
import com.thindie.avezer.engine.stateSink
import com.thindie.avezer.engine.sub
import com.thindie.avezer.engine.transition
import com.thindie.avezer.feature.home.domain.PlacesRepository
import com.thindie.avezer.feature.home.search.SearchScreenCommand
import com.thindie.avezer.feature.home.search.SearchScreenState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull

internal suspend fun SearchFlow.exec(
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
        finish(SearchFlow.Result.PlaceRequested(result))
      }

      state
    }

    is SearchScreenCommand.ToggleFavorite -> {
      flowModule.placesRepository.toggleFavorite(command.favoriteLocation)
      val favorites = flowModule.placesRepository.favoriteCities.firstOrNull().orEmpty()
      state.copy(favorites = favorites)
    }

    is SearchScreenCommand.OpenSearch -> {
      go(this@exec.SearchRoute)
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

internal fun ScreenScope<SearchScreenState, SearchScreenCommand>.subscriptions(placesRepository: PlacesRepository) {
  stateSink(this) { scope ->
    scope.sub(placesRepository.favoriteCities).transition(
      block = { s, favorites -> s.copy(favorites = favorites) },
    )
  }
}
