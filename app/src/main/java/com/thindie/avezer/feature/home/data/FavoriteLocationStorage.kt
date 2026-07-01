package com.thindie.avezer.feature.home.data

import com.google.gson.reflect.TypeToken
import com.thindie.avezer.application.LocationResolver
import com.thindie.avezer.application.storage.Storage
import com.thindie.avezer.engine.Log
import com.thindie.avezer.feature.home.domain.FavoriteLocation

internal object FavoriteLocationStorage {
  suspend fun read(
    storage: Storage,
    resolver: LocationResolver,
  ): List<FavoriteLocation> {
    val raw = storage.read(HomeStorageIds.Favorites) ?: return emptyList()

    val favoriteLocationsType = object : TypeToken<List<FavoriteLocation>>() {}.type
    @Suppress("UNCHECKED_CAST")
    return JsonUtil.fromJson<List<FavoriteLocation>>(raw, favoriteLocationsType)
      ?: run {
        Log.w({ "Migrating legacy favorite locations to JSON format." })
        val migrated =
          parseLegacyFavorites(raw)
            .mapNotNull { city -> resolver.read(city)?.let { FavoriteLocation(city, it.first, it.second) } }

        storage.createOrUpdate(HomeStorageIds.Favorites, JsonUtil.toJson(migrated))
        migrated
      }
  }

  private fun parseLegacyFavorites(raw: String): List<String> {
    return raw.split(HomeStorageIds.LegacyFavoritesSeparator).filter { it.isNotBlank() }
  }
}
