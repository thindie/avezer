package com.thindie.avezer.feature.home.places.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thindie.avezer.R
import com.thindie.avezer.feature.home.domain.Weather
import com.thindie.avezer.uikit.AppTheme
import com.thindie.avezer.uikit.VSpacer

@Composable
internal fun WeatherCard(
  weather: Weather,
  modifier: Modifier = Modifier,
) {
  Card(
    modifier = modifier.padding(8.dp),
    colors = CardDefaults.cardColors(containerColor = AppTheme.colors.backgroundSecondary),
    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
  ) {
    Column(
      modifier =
        Modifier
          .padding(16.dp)
          .fillMaxWidth(),
    ) {
      Text(
        text = weather.city,
        style = AppTheme.typography.headlineMedium,
        color = AppTheme.colors.contentPrimary,
      )

      VSpacer(12.dp)

      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Text(
          text = "${weather.temperature.toInt()}°C",
          fontSize = 64.sp,
          color = AppTheme.colors.contentSecondary,
        )

        Text(
          text = weather.emoji,
          fontSize = 72.sp,
        )
      }

      VSpacer(8.dp)

      Text(
        text = stringResource(weather.weatherCodeRef),
        style = AppTheme.typography.titleMedium,
        color = AppTheme.colors.contentPrimary,
      )

      if (weather.windSpeed != null || weather.humidity != null) {
        VSpacer(16.dp)
        Divider(color = AppTheme.colors.backgroundSecondary)
        VSpacer(12.dp)

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          weather.humidity?.let { h ->
            WeatherMetricItem(
              label = stringResource(id = R.string.weather_humidity),
              value = "$h%",
            )
          }
          weather.windSpeed?.let { w ->
            WeatherMetricItem(
              label = stringResource(R.string.weather_wind_speed),
              value = "${w.toInt()} km/h",
            )
          }
        }
      }
    }
  }
}

@Composable
private fun WeatherMetricItem(
  label: String,
  value: String,
) {
  Column {
    Text(
      text = label,
      style = AppTheme.typography.bodyMedium,
      color = AppTheme.colors.contentPrimary,
    )
    Text(
      text = value,
      style = AppTheme.typography.titleMedium,
      color = AppTheme.colors.contentSecondary,
    )
  }
}
