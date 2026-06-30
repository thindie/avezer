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
import androidx.compose.ui.tooling.preview.Preview
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
internal fun PlaceHourlyScreen(screenScope: ScreenScope<PlaceHourlyState, PlaceHourlyCommand>) {
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
      onBack = { screenScope.send(PlaceHourlyCommand.Back) },
    )
  }
}

@Preview(name = "Place Hourly Preview")
@Composable
private fun PlaceHourlyPreview() {
  val mockHourlyForecast =
    listOf(
      HourlyForecastItem(
        time = "12:00",
        temperature = 24.5,
        humidity = 45,
        windSpeed = 8.3,
        precipitation = 0.0,
        weatherCodeRef = R.string.weather_code_0,
        emoji = "☀️",
      ),
      HourlyForecastItem(
        time = "15:00",
        temperature = 26.1,
        humidity = 40,
        windSpeed = 10.1,
        precipitation = 0.0,
        weatherCodeRef = R.string.weather_code_0,
        emoji = "☀️",
      ),
      HourlyForecastItem(
        time = "18:00",
        temperature = 22.3,
        humidity = 55,
        windSpeed = 6.7,
        precipitation = 0.5,
        weatherCodeRef = R.string.weather_code_2,
        emoji = "☁️",
      ),
      HourlyForecastItem(
        time = "21:00",
        temperature = 18.9,
        humidity = 68,
        windSpeed = 4.2,
        precipitation = 2.3,
        weatherCodeRef = R.string.weather_code_63,
        emoji = "🌧️",
      ),
    )

  PlaceHourlyContent(
    hourlyForecast = mockHourlyForecast,
    onBack = {},
  )
}

@Composable
private fun PlaceHourlyContent(
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
