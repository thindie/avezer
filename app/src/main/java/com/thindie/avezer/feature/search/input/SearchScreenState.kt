package com.thindie.avezer.feature.search.input

import androidx.compose.runtime.Immutable
import com.thindie.avezer.engine.ViewState
import com.thindie.avezer.feature.home.domain.FavoriteLocation
import com.thindie.avezer.feature.home.domain.Weather
import com.thindie.avezer.feature.home.domain.WeatherSearchResult

@Immutable
internal data class SearchScreenState(
  val query: String = "",
  val results: List<WeatherSearchResult> = emptyList(),
  val selectedCity: Weather? = null,
  val favorites: List<FavoriteLocation> = emptyList(),
) : ViewState
