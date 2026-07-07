package com.thindie.avezer.feature.settings.selection

import android.app.LocaleManager
import android.os.Build
import android.os.LocaleList
import com.thindie.avezer.engine.Command
import com.thindie.avezer.feature.home.domain.FavoriteLocation
import com.thindie.avezer.feature.settings.SettingsFlow
import com.thindie.avezer.feature.settings.domain.SettingsRepository

internal sealed interface SettingsCommand : Command {
  data object Back : SettingsCommand

  data class SetThemeChoice(val themeChoice: SettingsRepository.ThemeChoice) : SettingsCommand

  data class SelectLanguage(val languageCode: String) : SettingsCommand

  data object StartWithFavorites : SettingsCommand

  data class DeleteLocation(val location: FavoriteLocation) : SettingsCommand
}

internal suspend fun SettingsFlow.exec(
  command: SettingsCommand,
  state: SettingsState,
): SettingsState? =
  when (command) {
    is SettingsCommand.Back -> {
      back()
      null
    }

    is SettingsCommand.SelectLanguage -> {
      repository.setLanguage(command.languageCode)
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val localeManager = context.getSystemService(LocaleManager::class.java)
        localeManager.applicationLocales = LocaleList.forLanguageTags(command.languageCode)
        state.copy(language = command.languageCode)
      } else {
        state.copy(language = command.languageCode, legacyRestart = true)
      }
    }

    is SettingsCommand.SetThemeChoice -> {
      repository.setThemeChoice(command.themeChoice)
      state.copy(themeChoice = command.themeChoice)
    }

    is SettingsCommand.StartWithFavorites -> {
      val newState = !state.startWithFavorites
      repository.toggleStartWithFavorite(newState)
      state.copy(startWithFavorites = newState)
    }

    is SettingsCommand.DeleteLocation -> {
      searchRepository.toggleFavorite(command.location)
      null
    }
  }
