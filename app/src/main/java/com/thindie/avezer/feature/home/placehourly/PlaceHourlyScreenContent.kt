package com.thindie.avezer.feature.home.placehourly

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.thindie.avezer.feature.home.domain.HourlyForecastItem
import com.thindie.avezer.feature.home.placehourly.components.HourlyForecastCard
import com.thindie.avezer.uikit.Action
import com.thindie.avezer.uikit.AppScreen
import com.thindie.avezer.uikit.AppTheme
import com.thindie.avezer.uikit.LocalThemeSwitcher
import com.thindie.avezer.uikit.ThemeSwitcher

@Composable
internal fun ScreenScope<PlaceHourlyState, PlaceHourlyCommand>.PlaceHourlyScreen() {
  val themeSwitcher = LocalThemeSwitcher.current
  val themeChoice by themeSwitcher.themeFlow.collectAsState(ThemeSwitcher.Choice.Auto)
  val isDark =
    when (themeChoice) {
      ThemeSwitcher.Choice.Dark -> true
      ThemeSwitcher.Choice.Light -> false
      else -> isSystemInDarkTheme()
    }

  val screenState by state.collectAsState()

  AppScreen(
    secondary =
      Action(
        resRef = R.drawable.ic_theme_24,
        listener = {
          themeSwitcher.set(
            if (isDark) ThemeSwitcher.Choice.Light else ThemeSwitcher.Choice.Dark,
          )
        },
      ),
  ) {
    PlaceHourlyContent(
      hourlyForecast = screenState.hourlyForecast,
      onBack = { send(PlaceHourlyCommand.Back) },
    )
  }
}

@Composable
internal fun PlaceHourlyContent(
  hourlyForecast: List<HourlyForecastItem>?,
  onBack: () -> Unit,
) {
  BackHandler { onBack() }

  if (hourlyForecast.isNullOrEmpty()) {
    Box(
      modifier =
        Modifier
          .fillMaxSize()
          .padding(16.dp),
      contentAlignment = Alignment.Center,
    ) {
      Text(
        text = stringResource(R.string.places_no_forecast),
        style = AppTheme.typography.bodyMedium,
        color = AppTheme.colors.contentSecondary,
      )
    }
  } else {
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
    ) {
      items(hourlyForecast) { forecast ->
        HourlyForecastCard(forecast)
      }
    }
  }
}
