package com.thindie.avezer.feature.home.data

import com.thindie.avezer.application.Address
import com.thindie.avezer.application.LocationResolver
import com.thindie.avezer.application.storage.Storage
import com.thindie.avezer.feature.home.domain.FavoriteLocation
import com.thindie.avezer.feature.home.domain.PlacesRepository
import com.thindie.avezer.feature.home.domain.WeatherSearchResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlacesRepositoryImpl(
  private val storage: Storage,
  private val scope: CoroutineScope,
  private val resolver: LocationResolver,
) : PlacesRepository {
  private data class Cache(val favorites: List<FavoriteLocation>)

  private val cache = MutableStateFlow(Cache(emptyList()))
  private val addressesRequest =
    MutableSharedFlow<List<Address>>(
      extraBufferCapacity = 3,
      onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

  init {
    scope.launch {
      cache.update { it.copy(favorites = FavoriteLocationStorage.read(storage, resolver)) }
    }
  }

  override suspend fun search(query: String) {
    val addresses = resolver.readAddresses(name = query)
    addressesRequest.tryEmit(addresses)
  }

  override val result: Flow<List<WeatherSearchResult>> =
    addressesRequest.mapLatest { addresses ->
      addresses.mapNotNull {
        val coordinates = resolver.read(it.name) ?: return@mapNotNull null
        WeatherSearchResult(
          city = it.name,
          lat = coordinates.first,
          lon = coordinates.second,
        )
      }
    }

  override suspend fun toggleFavorite(location: FavoriteLocation) {
    val currentFavorites = cache.value.favorites
    val updatedFavorites =
      if (currentFavorites.any { it == location }) {
        currentFavorites.filterNot { it == location }
      } else {
        currentFavorites + location
      }

    storage.createOrUpdate(HomeStorageIds.Favorites, JsonUtil.toJson(updatedFavorites))
    cache.update { it.copy(favorites = updatedFavorites) }
  }

  override val favoriteCities: Flow<List<FavoriteLocation>> = cache.map { it.favorites }
}
