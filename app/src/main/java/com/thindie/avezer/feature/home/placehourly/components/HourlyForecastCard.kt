package com.thindie.avezer.feature.home.placehourly.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thindie.avezer.R
import com.thindie.avezer.feature.home.domain.HourlyForecastItem
import com.thindie.avezer.uikit.AppTheme
import com.thindie.avezer.uikit.VSpacer

@Composable
internal fun HourlyForecastCard(
  item: HourlyForecastItem,
  modifier: Modifier = Modifier,
) {
  Card(
    modifier = modifier.padding(horizontal = 8.dp, vertical = 4.dp),
    colors = CardDefaults.cardColors(containerColor = AppTheme.colors.backgroundSecondary),
    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
  ) {
    Column(
      modifier =
        Modifier
          .padding(12.dp)
          .fillMaxWidth(),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Text(
          text = item.time,
          style = AppTheme.typography.titleMedium,
          color = AppTheme.colors.contentPrimary,
        )

        Text(
          text = "${item.temperature.toInt()}°C",
          fontSize = 48.sp,
          fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
          color = AppTheme.colors.accentPrimary,
        )
      }

      VSpacer(12.dp)

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        WeatherMetricItem(
          label = stringResource(id = R.string.weather_humidity),
          value =
            stringResource(
              id = R.string.percent_sign,
              (item.humidity ?: "-").toString(),
            ),
        )

        WeatherMetricItem(
          label = stringResource(id = R.string.weather_wind_speed),
          value =
            stringResource(
              id = R.string.kilometers_per_hour,
              item.windSpeed.toInt().toString(),
            ),
        )

        WeatherMetricItem(
          label = stringResource(id = R.string.weather_precipitation),
          value =
            stringResource(
              id = R.string.millimeter,
              item.precipitation.toInt().toString(),
            ),
        )
      }

      VSpacer(4.dp)

      Text(
        text = stringResource(item.weatherCodeRef),
        style = AppTheme.typography.labelMedium,
        color = AppTheme.colors.contentSecondary.copy(alpha = 0.8f),
      )
    }
  }
}

@Composable
private fun WeatherMetricItem(
  label: String,
  value: String,
) {
  Column(modifier = Modifier.padding(end = 16.dp)) {
    Text(
      text = label,
      style = AppTheme.typography.bodySmall,
      color = AppTheme.colors.contentSecondary,
    )
    Text(
      text = value,
      style = AppTheme.typography.titleMedium,
      color = AppTheme.colors.contentPrimary,
    )
  }
}
