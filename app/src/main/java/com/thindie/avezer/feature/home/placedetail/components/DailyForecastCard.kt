package com.thindie.avezer.feature.home.placedetail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.thindie.avezer.R
import com.thindie.avezer.feature.home.domain.DailyForecast
import com.thindie.avezer.feature.home.domain.TimeFormatter
import com.thindie.avezer.uikit.AppTheme
import com.thindie.avezer.uikit.VSpacer
import com.thindie.avezer.uikit.weather.WeatherColorMapper

@Composable
internal fun DailyForecastCard(
  dailyForecast: DailyForecast,
  modifier: Modifier = Modifier,
  onClick: () -> Unit = {},
) {
  val accentColor = WeatherColorMapper.getAccentColor(dailyForecast)

  Card(
    modifier =
      modifier
        .padding(horizontal = 8.dp, vertical = 4.dp)
        .clickable(onClick = onClick),
    colors = CardDefaults.cardColors(containerColor = AppTheme.colors.cardPrimary),
    shape = RoundedCornerShape(16.dp),
    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
  ) {
    Column(
      modifier =
        Modifier
          .padding(16.dp)
          .fillMaxWidth(),
    ) {
      // Header: Date and Weather Icon
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
          text = TimeFormatter.formatDailyDate(dailyForecast.time),
          style = AppTheme.typography.titleLarge,
          color = AppTheme.colors.contentPrimary,
        )

        Text(
          text = dailyForecast.emoji,
          style = AppTheme.typography.headlineLarge,
        )
      }

      VSpacer(12.dp)

      // Temperature Range
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = stringResource(R.string.weather_max_temp),
            style = AppTheme.typography.labelMedium,
            color = AppTheme.colors.contentSecondary,
          )
          VSpacer(2.dp)
          Text(
            text = "${dailyForecast.temperatureMax.toInt()}°",
            style = AppTheme.typography.headlineSmall,
            color = accentColor,
          )
        }

        // Temperature Range Indicator
        Box(
          modifier =
            Modifier
              .padding(horizontal = 8.dp)
              .clip(RoundedCornerShape(4.dp))
              .background(accentColor.copy(alpha = 0.2f))
              .padding(horizontal = 12.dp, vertical = 6.dp),
        ) {
          Text(
            text = "${dailyForecast.temperatureMax.toInt()}° / ${dailyForecast.temperatureMin.toInt()}°",
            style = AppTheme.typography.titleSmall,
            color = accentColor,
          )
        }

        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
          Text(
            text = stringResource(R.string.weather_min_temp),
            style = AppTheme.typography.labelMedium,
            color = AppTheme.colors.contentSecondary,
          )
          VSpacer(2.dp)
          Text(
            text = "${dailyForecast.temperatureMin.toInt()}°",
            style = AppTheme.typography.headlineSmall,
            color = accentColor.copy(alpha = 0.7f),
          )
        }
      }

      // Precipitation Section
      if (dailyForecast.precipitationSum > 0) {
        VSpacer(12.dp)

        HorizontalDivider(color = AppTheme.colors.backgroundSecondary, thickness = 0.5.dp)
        VSpacer(8.dp)

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Text(
            text = stringResource(R.string.weather_precipitation),
            style = AppTheme.typography.labelMedium,
            color = AppTheme.colors.contentSecondary,
          )

          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
          ) {
            Text(
              text = "💧",
              style = AppTheme.typography.bodyMedium,
            )
            Text(
              text = "${dailyForecast.precipitationSum} ${stringResource(R.string.millimeter)}",
              style = AppTheme.typography.titleSmall,
              color = accentColor,
            )
          }
        }
      }

      // Sunrise/Sunset Section
      if (dailyForecast.sunrise != null || dailyForecast.sunset != null) {
        VSpacer(12.dp)

        HorizontalDivider(color = AppTheme.colors.backgroundSecondary, thickness = 0.5.dp)
        VSpacer(8.dp)

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Text(
            text = stringResource(R.string.weather_sunrise),
            style = AppTheme.typography.labelMedium,
            color = AppTheme.colors.contentSecondary,
          )

          Text(
            text =
              dailyForecast.sunrise?.let { TimeFormatter.formatTime(it) }
                ?: stringResource(R.string.weather_not_available),
            style = AppTheme.typography.titleSmall,
            color = accentColor.copy(alpha = 0.8f),
          )
        }

        VSpacer(6.dp)

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Text(
            text = stringResource(R.string.weather_sunset),
            style = AppTheme.typography.labelMedium,
            color = AppTheme.colors.contentSecondary,
          )

          Text(
            text =
              dailyForecast.sunset?.let { TimeFormatter.formatTime(it) }
                ?: stringResource(R.string.weather_not_available),
            style = AppTheme.typography.titleSmall,
            color = accentColor.copy(alpha = 0.8f),
          )
        }
      }
    }
  }
}
