package com.thindie.avezer.feature.home.placedetail.components

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
import com.thindie.avezer.R
import com.thindie.avezer.feature.home.domain.DailyForecast
import com.thindie.avezer.uikit.AppTheme
import com.thindie.avezer.uikit.VSpacer

@Composable
internal fun DailyForecastCard(
  dailyForecast: DailyForecast,
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
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
          text = dailyForecast.time,
          style = AppTheme.typography.headlineSmall,
          color = AppTheme.colors.contentPrimary,
        )

        Text(
          text = dailyForecast.emoji,
          style = AppTheme.typography.headlineMedium,
        )
      }

      Column {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Text(
            text = stringResource(R.string.weather_max_temp),
            style = AppTheme.typography.bodySmall,
            color = AppTheme.colors.contentSecondary,
          )

          Text(
            text = "${dailyForecast.temperatureMax.toInt()}°",
            style = AppTheme.typography.titleMedium,
            color = AppTheme.colors.contentPrimary,
          )
        }

        VSpacer(4.dp)

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Text(
            text = stringResource(R.string.weather_min_temp),
            style = AppTheme.typography.bodySmall,
            color = AppTheme.colors.contentSecondary,
          )

          Text(
            text = "${dailyForecast.temperatureMin.toInt()}°",
            style = AppTheme.typography.titleMedium,
            color = AppTheme.colors.contentPrimary,
          )
        }
      }

      if (dailyForecast.precipitationSum > 0) {
        VSpacer(6.dp)

        Divider(color = AppTheme.colors.backgroundSecondary)
        VSpacer(8.dp)

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Text(
            text = stringResource(R.string.weather_precipitation),
            style = AppTheme.typography.bodySmall,
            color = AppTheme.colors.contentSecondary,
          )

          Text(
            text = "${dailyForecast.precipitationSum} ${stringResource(R.string.millimeter)}",
            style = AppTheme.typography.titleMedium,
            color = AppTheme.colors.contentPrimary,
          )
        }
      }

      if (dailyForecast.sunrise != null || dailyForecast.sunset != null) {
        VSpacer(6.dp)

        Divider(color = AppTheme.colors.backgroundSecondary)
        VSpacer(8.dp)

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Text(
            text = stringResource(R.string.weather_sunrise),
            style = AppTheme.typography.bodySmall,
            color = AppTheme.colors.contentSecondary,
          )

          Text(
            text = dailyForecast.sunrise ?: "--:--",
            style = AppTheme.typography.titleMedium,
            color = AppTheme.colors.contentPrimary,
          )
        }

        VSpacer(4.dp)

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Text(
            text = stringResource(R.string.weather_sunset),
            style = AppTheme.typography.bodySmall,
            color = AppTheme.colors.contentSecondary,
          )

          Text(
            text = dailyForecast.sunset ?: "--:--",
            style = AppTheme.typography.titleMedium,
            color = AppTheme.colors.contentPrimary,
          )
        }
      }
    }
  }
}
