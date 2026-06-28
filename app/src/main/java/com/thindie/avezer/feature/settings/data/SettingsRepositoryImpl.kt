package com.thindie.avezer.feature.settings.data

import com.thindie.avezer.application.storage.Storage
import com.thindie.avezer.application.storage.StorageId
import com.thindie.avezer.feature.settings.domain.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsRepositoryImpl(
  private val storage: Storage,
  private val scope: CoroutineScope,
) : SettingsRepository {
  private data class Cache(
    val language: String? = null,
    val startWithFavorite: Boolean = false,
  )

  private val cache = MutableStateFlow(Cache())

  init {
    scope.launch {
      cache.value =
        Cache(
          language = storage.read(LANGUAGE_KEY),
          startWithFavorite = storage.read(FAVORITE_KEY) == "true",
        )
    }
  }

  override val startWithFavorite: Flow<Boolean> = cache.map { it.startWithFavorite }

  override fun language(): String? = cache.value.language

  override fun setLanguage(code: String) {
    cache.update { it.copy(language = code) }
    scope.launch { storage.createOrUpdate(LANGUAGE_KEY, code) }
  }

  override fun isStartWithFavoriteProfilesEnabled(): Boolean = cache.value.startWithFavorite

  override suspend fun toggleStartWithFavorite(enabled: Boolean) {
    cache.update { it.copy(startWithFavorite = enabled) }
    storage.createOrUpdate(FAVORITE_KEY, enabled.toString())
  }

  companion object {
    private val LANGUAGE_KEY = StorageId("settings_language")
    private val FAVORITE_KEY = StorageId("settings_favorite")
  }
}
