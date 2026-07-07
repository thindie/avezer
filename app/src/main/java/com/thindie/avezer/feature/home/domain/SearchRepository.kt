package com.thindie.avezer.feature.home.domain

import kotlinx.coroutines.flow.Flow

interface SearchRepository {
  suspend fun search(query: String)

  val result: Flow<List<WeatherSearchResult>>

  suspend fun toggleFavorite(location: FavoriteLocation)

  val favorites: Flow<List<FavoriteLocation>>

  suspend fun fetchFavorites()
}
