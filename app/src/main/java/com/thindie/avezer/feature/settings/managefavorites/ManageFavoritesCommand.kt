package com.thindie.avezer.feature.settings.managefavorites

import com.thindie.avezer.engine.Command
import com.thindie.avezer.feature.home.domain.FavoriteLocation

internal sealed interface ManageFavoritesCommand : Command {
  data object Back : ManageFavoritesCommand

  data object Init : ManageFavoritesCommand

  data class DeleteLocation(val location: FavoriteLocation) : ManageFavoritesCommand

  data class SetFavoriteLocation(val location: FavoriteLocation) : ManageFavoritesCommand
}
