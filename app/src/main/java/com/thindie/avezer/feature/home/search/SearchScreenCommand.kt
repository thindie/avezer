package com.thindie.avezer.feature.home.search

import com.thindie.avezer.engine.Command
import com.thindie.avezer.feature.home.domain.FavoriteLocation
import com.thindie.avezer.feature.home.domain.WeatherSearchResult

internal sealed interface SearchScreenCommand : Command {
  data class Search(val query: String) : SearchScreenCommand

  data object ConfirmSearch : SearchScreenCommand

  data class SelectCity(val result: WeatherSearchResult) : SearchScreenCommand

  data class ToggleFavorite(val favoriteLocation: FavoriteLocation) : SearchScreenCommand

  data object OpenSearch : SearchScreenCommand

  data object Back : SearchScreenCommand
}
