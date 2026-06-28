package com.thindie.avezer.feature.home.data

import com.thindie.avezer.application.Address
import com.thindie.avezer.application.LocationResolver
import com.thindie.avezer.application.storage.Storage
import com.thindie.avezer.application.storage.StorageId
import com.thindie.avezer.feature.home.domain.PlacesRepository
import com.thindie.avezer.feature.home.domain.WeatherSearchResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlacesRepositoryImpl(
  private val storage: Storage,
  private val scope: CoroutineScope,
  private val resolver: LocationResolver,
) : PlacesRepository {
  private data class Cache(val favorites: List<String>)

  private val cache = MutableStateFlow(Cache(emptyList()))
  private val addressesRequest =
    MutableSharedFlow<List<Address>>(
      extraBufferCapacity = 3,
      onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

  init {
    scope.launch {
      val favs = storage.read(FAVORITES_KEY)
      cache.update { it.copy(favorites = parseFavorites(favs)) }
    }
  }

  override suspend fun search(query: String) {
    val addresses = resolver.readAddresses(name = query)
    addressesRequest.tryEmit(addresses)
  }

  override val result: Flow<List<WeatherSearchResult>> =
    addressesRequest.mapLatest {
      it.mapNotNull {
        val coordinates = resolver.read(it.name) ?: return@mapNotNull null
        WeatherSearchResult(
          city = it.name,
          lat = coordinates.first,
          lon = coordinates.second,
          isFavorite = it.name in cache.value.favorites,
        )
      }
    }

  override suspend fun toggleFavorite(city: String) {
    cache.update { favs ->
      val updated = if (city in favs.favorites) favs.favorites - city else favs.favorites + city
      scope.launch { storage.createOrUpdate(FAVORITES_KEY, updated.joinToString(SEPARATOR)) }
      favs.copy(favorites = updated)
    }
  }

  companion object {
    private val FAVORITES_KEY = StorageId("places_favorites")
    private const val SEPARATOR = "#$#$#$#$#$#"
  }

  private fun parseFavorites(raw: String?): List<String> {
    return raw?.split(SEPARATOR)?.filter { it.isNotBlank() } ?: emptyList()
  }
}
