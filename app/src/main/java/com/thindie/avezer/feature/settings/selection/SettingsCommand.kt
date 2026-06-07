package com.thindie.avezer.feature.settings.selection

import com.thindie.avezer.engine.Command
import com.thindie.avezer.feature.settings.SettingsFlow

internal sealed interface SettingsCommand : Command {
  data object Back : SettingsCommand

  data class SetThemeChoice(val themeChoice: SettingsState.ThemeChoice) : SettingsCommand
}

internal fun SettingsFlow.exec(
  command: SettingsCommand,
  state: SettingsState,
): SettingsState =
  when (command) {
    is SettingsCommand.Back -> {
      back()
      state
    }

    is SettingsCommand.SetThemeChoice -> {
      state.copy(themeChoice = command.themeChoice)
    }
  }
