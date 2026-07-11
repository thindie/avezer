package com.thindie.avezer.feature.home.placedetail

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.thindie.avezer.R
import com.thindie.avezer.engine.ScreenScope
import com.thindie.avezer.feature.home.domain.DailyForecast
import com.thindie.avezer.feature.home.domain.TimeFormatter
import com.thindie.avezer.feature.home.placedetail.components.DailyForecastCard
import com.thindie.avezer.feature.home.placedetail.components.TemperatureBarChart
import com.thindie.avezer.uikit.Action
import com.thindie.avezer.uikit.AppScreen
import com.thindie.avezer.uikit.AppTheme
import com.thindie.avezer.uikit.HSpacer
import com.thindie.avezer.uikit.VSpacer

@Composable
internal fun PlaceDetailScreen(screenScope: ScreenScope<PlaceDetailState, PlaceDetailCommand>) {
  val screenState by screenScope.state.collectAsState()

  AppScreen(
    screenScope = screenScope,
    primary =
      Action(
        resRef = R.drawable.ic_arrow_back_24,
        listener = { screenScope.send(PlaceDetailCommand.Back) },
      ),
  ) {
    PullToRefreshBox(
      isRefreshing =
        screenScope.processing.value is PlaceDetailCommand.Refresh ||
          screenScope.processing.value is PlaceDetailCommand.Fetch,
      onRefresh = { screenScope.send(PlaceDetailCommand.Refresh) },
      indicator = {},
    ) {
      PlaceDetailContent(
        title = screenState.title.orEmpty(),
        lastUpdated = screenState.lastUpdated,
        dailyForecast = screenState.dailyForecast,
        onBack = { screenScope.send(PlaceDetailCommand.Back) },
        onClick = { screenScope.send(PlaceDetailCommand.SeeHourlyForecast(it)) },
      )
    }
  }
}

@Preview(name = "Place Detail Preview")
@Composable
private fun PlaceDetailPreview() {
  val mockDailyForecast =
    listOf(
      com.thindie.avezer.feature.home.domain.DailyForecast(
        time = "2025-07-01",
        temperatureMax = 25.0,
        temperatureMin = 18.0,
        weatherCodeRef = R.string.weather_code_0,
        emoji = "☀️",
        sunrise = "06:15",
        sunset = "20:45",
        precipitationSum = 0.0,
      ),
      com.thindie.avezer.feature.home.domain.DailyForecast(
        time = "2025-07-02",
        temperatureMax = 23.5,
        temperatureMin = 17.2,
        weatherCodeRef = R.string.weather_code_2,
        emoji = "☁️",
        sunrise = "06:16",
        sunset = "20:44",
        precipitationSum = 2.5,
      ),
      com.thindie.avezer.feature.home.domain.DailyForecast(
        time = "2025-07-03",
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
    title = "Moscow",
    lastUpdated = System.currentTimeMillis(),
    dailyForecast = mockDailyForecast,
    onBack = {},
    onClick = {},
  )
}

@Composable
internal fun PlaceDetailContent(
  title: String,
  lastUpdated: Long?,
  dailyForecast: List<DailyForecast>?,
  onBack: () -> Unit,
  onClick: (DailyForecast) -> Unit = {},
) {
  BackHandler { onBack() }

  Column(
    modifier =
      Modifier
        .fillMaxSize()
        .padding(16.dp),
  ) {
    Text(
      text = title,
      style = AppTheme.typography.headlineLarge,
      color = AppTheme.colors.contentPrimary,
    )
    Text(
      text = stringResource(R.string.place_detail_forecast_title),
      style = AppTheme.typography.bodySmall,
      color = AppTheme.colors.contentSecondary,
    )
    if (lastUpdated != null) {
      val lastUpdatedTime = TimeFormatter.formatRelativeTime(lastUpdated, LocalContext.current)
      Row(
        modifier = Modifier.padding(top = 4.dp),
        verticalAlignment = Alignment.Bottom,
      ) {
        Text(
          text = stringResource(R.string.last_updated_label),
          style = AppTheme.typography.labelMedium,
          color = AppTheme.colors.contentSecondary,
        )
        HSpacer(4.dp)
        Text(
          text = lastUpdatedTime,
          style = AppTheme.typography.labelMedium,
          color = AppTheme.colors.contentSecondary,
        )
      }
    }

    VSpacer(24.dp)

    if (dailyForecast.isNullOrEmpty()) {
      Column(modifier = Modifier.fillMaxSize()) {}
    } else {
      Column(modifier = Modifier.fillMaxSize()) {
        // Temperature Bar Chart
        TemperatureBarChart(
          dailyForecasts = dailyForecast,
          modifier = Modifier.padding(horizontal = 16.dp),
        )

        VSpacer(8.dp)

        LazyColumn(
          modifier = Modifier.fillMaxSize(),
        ) {
          items(dailyForecast) { forecast ->
            DailyForecastCard(
              dailyForecast = forecast,
              onClick = { onClick(forecast) },
            )
          }
        }
      }
    }
  }
}
