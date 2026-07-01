package com.thindie.avezer.feature.home.domain

import kotlinx.coroutines.flow.Flow

interface PlacesRepository {
  suspend fun search(query: String)

  val result: Flow<List<WeatherSearchResult>>

  suspend fun toggleFavorite(location: FavoriteLocation)

  val favoriteCities: Flow<List<FavoriteLocation>>
}
