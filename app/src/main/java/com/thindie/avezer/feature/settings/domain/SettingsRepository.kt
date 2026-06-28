package com.thindie.avezer.feature.settings.domain

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
  fun language(): String?

  fun setLanguage(code: String)

  fun isStartWithFavoriteProfilesEnabled(): Boolean

  suspend fun toggleStartWithFavorite(enabled: Boolean)

  val startWithFavorite: Flow<Boolean>
}
