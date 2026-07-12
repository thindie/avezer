package com.thindie.avezer.feature.settings.domain

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
  fun language(): String?

  fun setLanguage(code: String)

  fun isStartWithFavoriteProfilesEnabled(): Boolean

  suspend fun toggleStartWithFavorite(enabled: Boolean)

  val startWithFavorite: Flow<Boolean>

  fun themeChoice(): ThemeChoice?

  suspend fun setThemeChoice(choice: ThemeChoice)

  sealed interface ThemeChoice {
    data object Auto : ThemeChoice

    data object Light : ThemeChoice

    data object Dark : ThemeChoice
  }
}
