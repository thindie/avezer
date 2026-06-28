package com.thindie.avezer.feature.home.search

import androidx.compose.runtime.Immutable
import com.thindie.avezer.engine.ViewState
import com.thindie.avezer.feature.home.domain.Weather
import com.thindie.avezer.feature.home.domain.WeatherSearchResult

@Immutable
data class SearchScreenState(
  val query: String = "",
  val results: List<WeatherSearchResult> = emptyList(),
  val selectedCity: Weather? = null,
  val favorites: List<String> = emptyList(),
) : ViewState
