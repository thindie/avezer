package com.thindie.avezer.feature.settings.managefavorites

import com.thindie.avezer.feature.settings.SettingsFlow

internal suspend fun SettingsFlow.exec(
  command: ManageFavoritesCommand,
  state: ManageFavoritesState,
): ManageFavoritesState? =
  when (command) {
    is ManageFavoritesCommand.Back -> {
      back()
      null
    }

    is ManageFavoritesCommand.DeleteLocation -> {
      searchRepository.toggleFavorite(command.location)
      null
    }

    is ManageFavoritesCommand.SetFavoriteLocation -> {
      searchRepository.setPrioritizedLocation(command.location)
      null
    }

    ManageFavoritesCommand.Init -> {
      searchRepository.fetchFavorites()
      null
    }
  }
