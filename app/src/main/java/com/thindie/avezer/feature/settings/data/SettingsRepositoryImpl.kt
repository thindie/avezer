package com.thindie.avezer.feature.settings.data

import com.thindie.avezer.application.storage.Storage
import com.thindie.avezer.application.storage.StorageId
import com.thindie.avezer.engine.Log
import com.thindie.avezer.feature.settings.domain.SettingsRepository
import kotlinx.coroutines.CancellationException
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
    val themeChoice: SettingsRepository.ThemeChoice? = null,
  )

  private val cache = MutableStateFlow(Cache())

  init {
    scope.launch {
      cache.value =
        Cache(
          language = storage.read(LANGUAGE_KEY),
          startWithFavorite = storage.read(FAVORITE_KEY) == "true",
          themeChoice = parseThemeChoice(storage.read(THEME_KEY)),
        )
    }
  }

  override val startWithFavorite: Flow<Boolean> = cache.map { it.startWithFavorite }

  override fun language(): String? = cache.value.language

  override fun setLanguage(code: String) {
    cache.update { it.copy(language = code) }
    safeWrite(LANGUAGE_KEY, code)
  }

  override fun isStartWithFavoriteProfilesEnabled(): Boolean = cache.value.startWithFavorite

  override suspend fun toggleStartWithFavorite(enabled: Boolean) {
    cache.update { it.copy(startWithFavorite = enabled) }
    safeWrite(FAVORITE_KEY, enabled.toString())
  }

  override fun themeChoice(): SettingsRepository.ThemeChoice? = cache.value.themeChoice

  override suspend fun setThemeChoice(choice: SettingsRepository.ThemeChoice) {
    cache.update { it.copy(themeChoice = choice) }
    safeWrite(THEME_KEY, themeChoiceToString(choice))
  }

  private fun themeChoiceToString(choice: SettingsRepository.ThemeChoice): String =
    when (choice) {
      is SettingsRepository.ThemeChoice.Auto -> "Auto"
      is SettingsRepository.ThemeChoice.Light -> "Light"
      is SettingsRepository.ThemeChoice.Dark -> "Dark"
    }

  private fun safeWrite(
    key: StorageId,
    value: String,
  ) {
    scope.launch {
      try {
        storage.createOrUpdate(key, value)
      } catch (e: CancellationException) {
        Log.e({ "Error writing settings for key ${key.value}" }, throwable = e)
        throw e
      } catch (e: Exception) {
        Log.e({ "Error writing settings for key ${key.value}" }, throwable = e)
      }
    }
  }

  companion object {
    private val LANGUAGE_KEY = StorageId("settings_language")
    private val FAVORITE_KEY = StorageId("settings_favorite")
    private val THEME_KEY = StorageId("settings_theme")

    private fun parseThemeChoice(value: String?): SettingsRepository.ThemeChoice? {
      if (value == null) return null
      return when (value) {
        "Auto" -> SettingsRepository.ThemeChoice.Auto
        "Light" -> SettingsRepository.ThemeChoice.Light
        "Dark" -> SettingsRepository.ThemeChoice.Dark
        else -> null
      }
    }
  }
}
