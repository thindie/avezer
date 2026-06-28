package com.thindie.avezer.feature.settings.selection

import androidx.compose.runtime.Immutable
import com.thindie.avezer.engine.ViewState

@Immutable
data class SettingsState(
  val themeChoice: ThemeChoice? = null,
  val language: String? = null,
  val legacyRestart: Boolean = false,
) : ViewState {
  @Immutable
  sealed interface ThemeChoice {
    data object Auto : ThemeChoice

    data object Light : ThemeChoice

    data object Dark : ThemeChoice
  }
}
