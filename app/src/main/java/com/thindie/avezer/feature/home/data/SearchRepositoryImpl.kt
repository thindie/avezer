package com.thindie.avezer.feature.home.data

import com.thindie.avezer.application.LocationResolver
import com.thindie.avezer.application.storage.Storage
import com.thindie.avezer.application.storage.StorageId
import com.thindie.avezer.engine.Log
import com.thindie.avezer.feature.home.domain.FavoriteLocation
import com.thindie.avezer.feature.home.domain.SearchRepository
import com.thindie.avezer.feature.home.domain.WeatherSearchResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

class SearchRepositoryImpl(
  private val storage: Storage,
  private val resolver: LocationResolver,
) : SearchRepository {
  private val addressesRequest = MutableStateFlow(emptyList<WeatherSearchResult>())
  private val favoritesCache = MutableStateFlow(emptyList<FavoriteLocation>())

  override val favorites: Flow<List<FavoriteLocation>> = favoritesCache

  override suspend fun search(query: String) {
    withContext(Dispatchers.IO) {
      val addresses = resolver.readAddresses(name = query)
      val favorite = readFavoritesInternal()
      favoritesCache.update { favorite }

      val addressesResult =
        addresses.map {
          WeatherSearchResult(
            city = it.name,
            lat = it.lat,
            lon = it.lon,
            isFavourite = favorite.any { favorite -> it.name == favorite.city },
          )
        }
      addressesRequest.update { addressesResult }
    }
  }

  override suspend fun fetchFavorites() {
    val favorite = readFavoritesInternal()
    favoritesCache.update { favorite }
  }

  override suspend fun setPrioritizedLocation(location: FavoriteLocation) {
    val cacheValue = readFavoritesInternal()
    val updatedFavorites =
      cacheValue.map {
        val fav =
          when {
            it.usedForWidget -> it.copy(usedForWidget = false)
            it.city == location.city -> it.copy(usedForWidget = true)
            else -> it
          }
        return@map fav
      }

    val storedFavorites =
      updatedFavorites
        .map { JsonUtil.toJson(it) }
        .onEach { Log.d({ "weather json: $it" }) }
        .joinToString(FAVORITE_PLACES_SEPARATOR)

    storage.createOrUpdate(favoriteStorageId, storedFavorites)
    favoritesCache.update { updatedFavorites }
  }

  private suspend fun readFavoritesInternal() =
    storage.read(favoriteStorageId)
      .orEmpty()
      .split(FAVORITE_PLACES_SEPARATOR)
      .mapNotNull { JsonUtil.fromJson(it, FavoriteLocation::class.java) }

  override val result: Flow<List<WeatherSearchResult>> = addressesRequest

  override suspend fun toggleFavorite(location: FavoriteLocation) {
    val cacheValue = readFavoritesInternal()
    val value =
      if (location in cacheValue) {
        cacheValue - location
      } else {
        cacheValue + location
      }

    val updatedFavorites =
      value.map {
        val sanitizedFavorite =
          it.copy(
            city = it.city,
            lat = formatCoordinate(it.lat).toDouble(),
            lon = formatCoordinate(it.lon).toDouble(),
          )
        JsonUtil.toJson(sanitizedFavorite)
      }
        .onEach { Log.d({ "weather json: $it" }) }
        .joinToString(FAVORITE_PLACES_SEPARATOR)

    storage.createOrUpdate(favoriteStorageId, updatedFavorites)
    favoritesCache.update { value }
    addressesRequest.update {
      it.map { result ->
        if (result.city == location.city) {
          result.copy(isFavourite = !result.isFavourite)
        } else {
          result
        }
      }
    }
  }
}

internal val favoriteStorageId get() = StorageId("places_favorites")

internal const val FAVORITE_PLACES_SEPARATOR = "#$12$#"
