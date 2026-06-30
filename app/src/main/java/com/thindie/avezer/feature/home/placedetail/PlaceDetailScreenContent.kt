package com.thindie.avezer.feature.home.placedetail

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.thindie.avezer.R
import com.thindie.avezer.engine.ScreenScope
import com.thindie.avezer.feature.home.placedetail.components.DailyForecastCard
import com.thindie.avezer.uikit.AppScreen

@Composable
internal fun PlaceDetailScreen(screenScope: ScreenScope<PlaceDetailState, PlaceDetailCommand>) {
  val screenState by screenScope.state.collectAsState()

  AppScreen(
    screenScope = screenScope,
  ) {
    PlaceDetailContent(
      dailyForecast = screenState.dailyForecast,
      onBack = { screenScope.send(PlaceDetailCommand.Back) },
    )
  }
}

@Preview(name = "Place Detail Preview")
@Composable
private fun PlaceDetailPreview() {
  val mockDailyForecast =
    listOf(
      com.thindie.avezer.feature.home.domain.DailyForecast(
        time = "Mon",
        temperatureMax = 25.0,
        temperatureMin = 18.0,
        weatherCodeRef = R.string.weather_code_0,
        emoji = "☀️",
        sunrise = "06:15",
        sunset = "20:45",
        precipitationSum = 0.0,
      ),
      com.thindie.avezer.feature.home.domain.DailyForecast(
        time = "Tue",
        temperatureMax = 23.5,
        temperatureMin = 17.2,
        weatherCodeRef = R.string.weather_code_2,
        emoji = "☁️",
        sunrise = "06:16",
        sunset = "20:44",
        precipitationSum = 2.5,
      ),
      com.thindie.avezer.feature.home.domain.DailyForecast(
        time = "Wed",
        temperatureMax = 20.1,
        temperatureMin = 15.8,
        weatherCodeRef = R.string.weather_code_63,
        emoji = "🌧️",
        sunrise = "06:17",
        sunset = "20:43",
        precipitationSum = 8.3,
      ),
    )

  PlaceDetailContent(
    dailyForecast = mockDailyForecast,
    onBack = {},
  )
}

@Composable
internal fun PlaceDetailContent(
  dailyForecast: List<com.thindie.avezer.feature.home.domain.DailyForecast>?,
  onBack: () -> Unit,
) {
  BackHandler { onBack() }

  if (dailyForecast.isNullOrEmpty()) {
    Column(modifier = Modifier.fillMaxSize()) {}
  } else {
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
    ) {
      items(dailyForecast) { forecast ->
        DailyForecastCard(dailyForecast = forecast)
      }
    }
  }
}
