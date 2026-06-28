package com.thindie.avezer.feature.home.places

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.thindie.avezer.R
import com.thindie.avezer.engine.ScreenScope
import com.thindie.avezer.feature.home.domain.Weather
import com.thindie.avezer.feature.home.places.components.WeatherCard
import com.thindie.avezer.uikit.AppScreen
import com.thindie.avezer.uikit.AppTheme
import com.thindie.avezer.uikit.LocalThemeSwitcher
import com.thindie.avezer.uikit.ThemeSwitcher
import com.thindie.avezer.uikit.VSpacer

@Composable
internal fun PlacesScreen(screenScope: ScreenScope<ScreenState, ScreenCommand>) {
  val themeSwitcher = LocalThemeSwitcher.current
  val themeChoice by themeSwitcher.themeFlow.collectAsState(ThemeSwitcher.Choice.Auto)
  val isDark =
    when (themeChoice) {
      ThemeSwitcher.Choice.Dark -> true
      ThemeSwitcher.Choice.Light -> false
      else -> isSystemInDarkTheme()
    }

  val screenState by screenScope.state.collectAsState()

  AppScreen(
    screenScope = screenScope,
  ) {
    PlacesContent(
      forecast = screenState.forecast,
      onBack = { screenScope.send(ScreenCommand.Back) },
      onClick = { screenScope.send(ScreenCommand.SeeHourlyForecast(it)) },
      onSearchClick = { screenScope.send(ScreenCommand.OpenSearch) },
    )
  }
}

@Composable
internal fun PlacesContent(
  forecast: List<Weather>?,
  onBack: () -> Unit,
  onClick: (Weather) -> Unit,
  onSearchClick: () -> Unit = {},
) {
  BackHandler { onBack() }
  if (forecast.isNullOrEmpty()) {
    Box(
      modifier =
        Modifier
          .fillMaxSize()
          .padding(16.dp),
      contentAlignment = Alignment.Center,
    ) {
      Column {
        Text(
          text = stringResource(R.string.places_no_forecast),
          style = AppTheme.typography.bodyMedium,
          color = AppTheme.colors.contentSecondary,
        )
        VSpacer(2.dp)
        TextButton(onClick = onSearchClick) {
          Text(
            text = stringResource(R.string.places_search_button),
            style = AppTheme.typography.bodyMedium,
            color = AppTheme.colors.accentPrimary,
          )
        }
      }
    }
  } else {
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
    ) {
      items(forecast) { weather ->
        WeatherCard(weather = weather) {
          onClick(weather)
        }
      }
    }
  }
}
