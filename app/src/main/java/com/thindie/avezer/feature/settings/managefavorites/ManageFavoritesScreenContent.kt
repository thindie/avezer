package com.thindie.avezer.feature.settings.managefavorites

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.thindie.avezer.R
import com.thindie.avezer.engine.ScreenScope
import com.thindie.avezer.engine.ServiceCommand
import com.thindie.avezer.feature.settings.selection.ToggleRow
import com.thindie.avezer.uikit.Action
import com.thindie.avezer.uikit.AppScreen
import com.thindie.avezer.uikit.AppTheme
import com.thindie.avezer.uikit.VSpacer

@Composable
internal fun ManageFavoritesScreenContent(scope: ScreenScope<ManageFavoritesState, ManageFavoritesCommand>) {
  AppScreen(
    primary =
      Action(
        resRef = R.drawable.ic_arrow_back_24,
        listener = { scope.send(ManageFavoritesCommand.Back) },
      ),
    screenScope = scope,
  ) {
    val state by scope.state.collectAsState()

    BackHandler { scope.send(ManageFavoritesCommand.Back) }

    Column(
      modifier =
        Modifier
          .fillMaxSize()
          .verticalScroll(rememberScrollState())
          .padding(16.dp),
    ) {
      Text(
        text = stringResource(R.string.settings_manage_favorites_title),
        style = AppTheme.typography.headlineLarge,
        color = AppTheme.colors.contentPrimary,
      )

      VSpacer(24.dp)

      // === Saved cities ===
      if (state.favorites.isNotEmpty()) {
        HorizontalDivider()
        VSpacer(16.dp)
        SectionTitle(stringResource(R.string.saved_favorite_cities))
        VSpacer(8.dp)
      }

      state.favorites.forEach { favorite ->
        SavedLocationItem(favorite.city) {
          scope.sendEvent(
            ServiceCommand.UiEvent.Decision(
              content = {
                DeleteLocationDialog(favorite.city)
              },
              primaryAction =
                Action(
                  resRef = R.string.btn_delete,
                  listener = { scope.send(ManageFavoritesCommand.DeleteLocation(favorite)) },
                ),
            ),
          )
        }
      }

      VSpacer(24.dp)

      // === Widget locations ===
      if (state.favorites.isNotEmpty()) {
        HorizontalDivider()
        VSpacer(16.dp)
        SectionTitle(stringResource(R.string.widget_favorite_cities))
        VSpacer(8.dp)
      }

      state.favorites.forEach { favoriteLocation ->
        ToggleRow(
          label = favoriteLocation.city,
          checked = favoriteLocation.usedForWidget,
          subtitle = favoriteLocation.lat.toString() + " : " + favoriteLocation.lon,
        ) { scope.send(ManageFavoritesCommand.SetFavoriteLocation(favoriteLocation)) }
      }
    }
  }
}

@Composable
private fun SavedLocationItem(
  cityName: String,
  onClick: (() -> Unit)? = null,
) {
  Row(
    modifier =
      Modifier
        .fillMaxWidth()
        .clickable(enabled = onClick != null, onClick = { onClick?.invoke() })
        .padding(12.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = cityName,
        style = AppTheme.typography.headlineSmall,
        color = AppTheme.colors.contentPrimary,
      )
    }
  }
}

// === Helper composables ===

@Composable
private fun SectionTitle(text: String) {
  Text(
    text = text,
    style = AppTheme.typography.titleMedium,
    color = AppTheme.colors.contentSecondary,
  )
}

@Composable
private fun DeleteLocationDialog(cityName: String) {
  Column {
    Text(
      text = stringResource(R.string.settings_delete_location_title),
      style = AppTheme.typography.headlineMedium,
      color = AppTheme.colors.contentPrimary,
    )
    VSpacer(16.dp)
    Text(
      text = stringResource(R.string.settings_delete_location_message, cityName),
      style = AppTheme.typography.bodyMedium,
      color = AppTheme.colors.contentSecondary,
    )
  }
}
