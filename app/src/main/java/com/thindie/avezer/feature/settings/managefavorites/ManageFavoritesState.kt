package com.thindie.avezer.feature.settings.managefavorites

import androidx.compose.runtime.Immutable
import com.thindie.avezer.engine.ViewState
import com.thindie.avezer.feature.home.domain.FavoriteLocation

@Immutable
internal data class ManageFavoritesState(
  val favorites: List<FavoriteLocation> = emptyList(),
) : ViewState
