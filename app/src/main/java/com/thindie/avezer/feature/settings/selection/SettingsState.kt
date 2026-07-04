package com.thindie.avezer.feature.settings.selection

import androidx.compose.runtime.Immutable
import com.thindie.avezer.engine.ViewState
import com.thindie.avezer.feature.settings.domain.SettingsRepository.ThemeChoice

@Immutable
data class SettingsState(
  val themeChoice: ThemeChoice? = null,
  val language: String? = null,
  val startWithFavorites: Boolean = false,
  val legacyRestart: Boolean = false,
) : ViewState
