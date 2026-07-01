package com.thindie.avezer.feature.home.places

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.thindie.avezer.R
import com.thindie.avezer.engine.ScreenScope
import com.thindie.avezer.feature.home.domain.Weather
import com.thindie.avezer.feature.home.places.components.WeatherCard
import com.thindie.avezer.uikit.AppScreen
import com.thindie.avezer.uikit.AppTheme
import com.thindie.avezer.uikit.VSpacer

@Composable
internal fun PlacesScreen(screenScope: ScreenScope<ScreenState, ScreenCommand>) {
  val screenState by screenScope.state.collectAsState()

  AppScreen(
    screenScope = screenScope,
  ) {
    val height = LocalWindowInfo.current.containerSize.height.dp
    PullToRefreshBox(
      modifier = Modifier.height(height),
      isRefreshing =
        screenScope.processing.value is ScreenCommand.Refresh,
      onRefresh = { screenScope.send(ScreenCommand.Refresh) },
      indicator = {},
    ) {
      PlacesContent(
        forecast = screenState.forecast,
        onBack = { screenScope.send(ScreenCommand.Back) },
        onClick = { screenScope.send(ScreenCommand.SeeDailyForecast(it)) },
        onSearchClick = { screenScope.send(ScreenCommand.OpenSearch) },
      )
    }
  }
}

@Preview(name = "Places Preview")
@Composable
private fun PlacesPreview() {
  val mockForecast =
    listOf(
      Weather(
        lat = 55.7558,
        lon = 37.6173,
        city = "Moscow",
        temperature = 22.5,
        isDay = true,
        weatherCodeRef = R.string.weather_code_0,
        emoji = "☀️",
        humidity = 45,
        windSpeed = 12.3,
        timezone = "Europe/Moscow",
        timezoneAbbreviation = "MSK",
        utcOffsetSeconds = 10800,
      ),
      Weather(
        lat = 59.9343,
        lon = 30.3351,
        city = "Saint Petersburg",
        temperature = 18.2,
        isDay = true,
        weatherCodeRef = R.string.weather_code_2,
        emoji = "☁️",
        humidity = 62,
        windSpeed = 8.7,
        timezone = "Europe/Moscow",
        timezoneAbbreviation = "MSK",
        utcOffsetSeconds = 10800,
      ),
      Weather(
        lat = 56.8389,
        lon = 60.6057,
        city = "Yekaterinburg",
        temperature = 15.8,
        isDay = false,
        weatherCodeRef = R.string.weather_code_63,
        emoji = "🌧️",
        humidity = 78,
        windSpeed = 15.2,
        timezone = "Asia/Yekaterinburg",
        timezoneAbbreviation = "YEKT",
        utcOffsetSeconds = 14400,
      ),
    )

  PlacesContent(
    forecast = mockForecast,
    onBack = {},
    onClick = {},
    onSearchClick = {},
  )
}

@Composable
private fun PlacesContent(
  forecast: List<Weather>?,
  onBack: () -> Unit,
  onClick: (Weather) -> Unit,
  onSearchClick: () -> Unit = {},
) {
  BackHandler { onBack() }

  Column(
    modifier =
      Modifier
        .fillMaxSize()
        .padding(16.dp),
  ) {
    Text(
      text = stringResource(R.string.places),
      style = AppTheme.typography.headlineLarge,
      color = AppTheme.colors.contentPrimary,
    )

    VSpacer(24.dp)
    Divider()
    VSpacer(16.dp)

    if (forecast.isNullOrEmpty()) {
      Column(modifier = Modifier.fillMaxSize()) {
        Text(
          text = stringResource(R.string.places_no_forecast),
          style = AppTheme.typography.bodyMedium,
          color = AppTheme.colors.contentSecondary,
        )
        VSpacer(8.dp)
        TextButton(onClick = onSearchClick) {
          Text(
            text = stringResource(R.string.places_search_button),
            style = AppTheme.typography.bodyMedium,
            color = AppTheme.colors.accentPrimary,
          )
        }
      }
    } else {
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        items(forecast) { weather ->
          WeatherCard(
            weather = weather,
            modifier = Modifier.padding(horizontal = 8.dp),
          ) {
            onClick(weather)
          }
        }
      }
    }
  }
}
