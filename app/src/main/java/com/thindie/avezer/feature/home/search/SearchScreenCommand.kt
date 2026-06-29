package com.thindie.avezer.feature.home.search

import com.thindie.avezer.engine.Command
import com.thindie.avezer.feature.home.domain.WeatherSearchResult

internal sealed interface SearchScreenCommand : Command {
  data class Search(val query: String) : SearchScreenCommand

  data object ConfirmSearch : SearchScreenCommand

  data object ClearSearch : SearchScreenCommand

  data object ClearQuery : SearchScreenCommand

  data class SelectCity(val result: WeatherSearchResult) : SearchScreenCommand

  data class ToggleFavorite(val city: String) : SearchScreenCommand

  data object OpenSearch : SearchScreenCommand

  data object Back : SearchScreenCommand
}
