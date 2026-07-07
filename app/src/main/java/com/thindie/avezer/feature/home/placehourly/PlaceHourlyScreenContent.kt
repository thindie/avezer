package com.thindie.avezer.feature.home.placehourly

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.thindie.avezer.R
import com.thindie.avezer.engine.ScreenScope
import com.thindie.avezer.feature.home.domain.DailyForecast
import com.thindie.avezer.feature.home.domain.HourlyForecastItem
import com.thindie.avezer.feature.home.domain.TimeFormatter
import com.thindie.avezer.feature.home.domain.Weather
import com.thindie.avezer.feature.home.placehourly.components.ExpandableHourlyCard
import com.thindie.avezer.uikit.Action
import com.thindie.avezer.uikit.AppScreen
import com.thindie.avezer.uikit.AppTheme
import com.thindie.avezer.uikit.HSpacer
import com.thindie.avezer.uikit.VSpacer

@Composable
internal fun PlaceHourlyScreen(screenScope: ScreenScope<PlaceHourlyState, PlaceHourlyCommand>) {
  val screenState by screenScope.state.collectAsState()

  AppScreen(
    screenScope = screenScope,
    primary =
      Action(
        resRef = R.drawable.ic_arrow_back_24,
        listener = { screenScope.send(PlaceHourlyCommand.Back) },
      ),
  ) {
    PullToRefreshBox(
      isRefreshing =
        screenScope.processing.value is PlaceHourlyCommand.Refresh,
      onRefresh = { screenScope.send(PlaceHourlyCommand.Refresh) },
      indicator = {},
    ) {
      val filteredForecast =
        if (screenState.hourlyStartIndex >= 0 && screenState.hourlyEndIndex > screenState.hourlyStartIndex) {
          screenState.hourlyForecast.subList(screenState.hourlyStartIndex, screenState.hourlyEndIndex)
        } else {
          screenState.hourlyForecast
        }
      PlaceHourlyContent(
        weather = screenState.weather,
        daily = screenState.dailyForecast,
        hourlyForecast = filteredForecast,
        onBack = { screenScope.send(PlaceHourlyCommand.Back) },
      )
    }
  }
}

@Preview(name = "Place Hourly Preview")
@Composable
private fun PlaceHourlyPreview() {
  val mockWeather =
    Weather(
      lat = 55.75,
      lon = 37.61,
      city = "Moscow",
      temperature = 24.0,
      isDay = true,
      weatherCodeRef = R.string.weather_code_0,
      emoji = "☀️",
      humidity = 45,
      windSpeed = 8.3,
      lastUpdated = System.currentTimeMillis(),
      timezone = "Europe/Moscow",
      timezoneAbbreviation = "MSK",
      utcOffsetSeconds = 10800,
    )

  val mockHourlyForecast =
    listOf(
      HourlyForecastItem(
        time = "2025-07-01T12:00",
        temperature = 24.5,
        humidity = 45,
        windSpeed = 8.3,
        precipitation = 0.0,
        weatherCodeRef = R.string.weather_code_0,
        emoji = "☀️",
      ),
      HourlyForecastItem(
        time = "2025-07-01T15:00",
        temperature = 26.1,
        humidity = 40,
        windSpeed = 10.1,
        precipitation = 0.0,
        weatherCodeRef = R.string.weather_code_0,
        emoji = "☀️",
      ),
      HourlyForecastItem(
        time = "2025-07-01T18:00",
        temperature = 22.3,
        humidity = 55,
        windSpeed = 6.7,
        precipitation = 0.5,
        weatherCodeRef = R.string.weather_code_2,
        emoji = "☁️",
      ),
      HourlyForecastItem(
        time = "2025-07-01T21:00",
        temperature = 18.9,
        humidity = 68,
        windSpeed = 4.2,
        precipitation = 2.3,
        weatherCodeRef = R.string.weather_code_63,
        emoji = "🌧️",
      ),
    )

  PlaceHourlyContent(
    weather = mockWeather,
    hourlyForecast = mockHourlyForecast,
    daily = mockWeather.forecast[1],
    onBack = {},
  )
}

@Composable
private fun PlaceHourlyContent(
  weather: Weather?,
  daily: DailyForecast,
  hourlyForecast: List<HourlyForecastItem>?,
  onBack: () -> Unit,
) {
  BackHandler { onBack() }

  Column(
    modifier =
      Modifier
        .fillMaxSize()
        .padding(16.dp),
  ) {
    // Title
    Text(
      text = weather?.city.orEmpty(),
      style = AppTheme.typography.headlineLarge,
      color = AppTheme.colors.contentPrimary,
    )

    if (weather != null) {
      VSpacer(4.dp)
      Text(
        text = stringResource(R.string.place_detail_hourly_forecast_title),
        style = AppTheme.typography.bodySmall,
        color = AppTheme.colors.contentSecondary,
      )
      val lastUpdatedTime = TimeFormatter.formatRelativeTime(weather.lastUpdated, LocalContext.current)
      Row(verticalAlignment = Alignment.CenterVertically) {
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

      VSpacer(16.dp)
      // Upper half: expanded current weather card
      DailyWeatherForecast(weather = daily)

      VSpacer(24.dp)

      if (hourlyForecast.isNullOrEmpty()) {
        Box(modifier = Modifier.fillMaxSize()) {}
      } else {
        val expandedStates = remember { mutableStateOf<BooleanArray>(BooleanArray(hourlyForecast.size) { false }) }

        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
          itemsIndexed(hourlyForecast, key = { index, _ -> index }) { index, forecast ->
            ExpandableHourlyCard(
              item = forecast,
              isExpanded = expandedStates.value[index],
              onToggle = {
                val newState = expandedStates.value.copyOf()
                newState[index] = !newState[index]
                expandedStates.value = newState
              },
            )
          }
        }
      }
    } else if (hourlyForecast != null && hourlyForecast.isNotEmpty()) {
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
      ) {
        itemsIndexed(hourlyForecast, key = { index, _ -> index }) { _, forecast ->
          ExpandableHourlyCard(item = forecast, isExpanded = false, onToggle = {})
        }
      }
    } else {
      Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
      ) {
        Text(
          text = stringResource(R.string.places_no_forecast),
          style = AppTheme.typography.bodyMedium,
          color = AppTheme.colors.contentSecondary,
        )
      }
    }
  }
}

@Composable
private fun DailyWeatherForecast(weather: DailyForecast) {
  val accentColor = com.thindie.avezer.uikit.weather.WeatherColorMapper.getAccentColor(weather.weatherCodeRef)

  Column(modifier = Modifier.padding(16.dp)) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Text(
        text = weather.emoji,
        style = AppTheme.typography.headlineLarge,
      )
      val temperature = (weather.temperatureMax + weather.temperatureMin) / 2
      Text(
        text = "${temperature.toInt()}°",
        style = AppTheme.typography.headlineLarge,
        color = accentColor,
      )
    }

    VSpacer(12.dp)

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.End,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
        Text(
          text = stringResource(R.string.weather_precipitation),
          style = AppTheme.typography.labelMedium,
          color = AppTheme.colors.contentSecondary,
        )
        VSpacer(2.dp)
        Text(
          text = "${weather.precipitationSum.toInt()} ${stringResource(R.string.millimeter)}",
          style = AppTheme.typography.titleSmall,
          color = accentColor.copy(alpha = 0.8f),
        )
      }
    }
  }
}
