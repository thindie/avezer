package com.thindie.avezer.feature.home.placehourly.components

import androidx.compose.foundation.background
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
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.thindie.avezer.R
import com.thindie.avezer.feature.home.domain.HourlyForecastItem
import com.thindie.avezer.feature.home.domain.TimeFormatter
import com.thindie.avezer.uikit.AppTheme
import com.thindie.avezer.uikit.VSpacer
import com.thindie.avezer.uikit.weather.WeatherColorMapper

@Composable
internal fun HourlyForecastCard(
  item: HourlyForecastItem,
  modifier: Modifier = Modifier,
) {
  val accentColor = WeatherColorMapper.getAccentColor(item.weatherCodeRef)

  Card(
    modifier = modifier.padding(horizontal = 8.dp, vertical = 4.dp),
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
      // Header: Time and Weather Icon
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
          text = TimeFormatter.formatHourlyTime(item.time),
          style = AppTheme.typography.titleLarge,
          color = AppTheme.colors.contentPrimary,
        )

        Box(
          modifier =
            Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(accentColor.copy(alpha = 0.1f))
              .padding(8.dp),
        ) {
          Text(
            text = item.emoji,
            style = AppTheme.typography.headlineLarge,
          )
        }
      }

      VSpacer(12.dp)

      // Current Temperature Section
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = stringResource(R.string.weather_current_temperature),
            style = AppTheme.typography.labelMedium,
            color = AppTheme.colors.contentSecondary,
          )
          VSpacer(2.dp)
          Text(
            text = "${item.temperature.toInt()}°",
            style = AppTheme.typography.headlineSmall,
            color = accentColor,
          )
        }

        // Precipitation Status
        val precipitationStatus = getPrecipitationStatus(item.weatherCodeRef)
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
          Text(
            text = precipitationStatus.emoji,
            style = AppTheme.typography.bodyMedium,
          )
          Text(
            text = stringResource(precipitationStatus.labelResId),
            style = AppTheme.typography.titleSmall,
            color = accentColor,
          )
        }
      }

      // Metrics Section
      VSpacer(12.dp)
      HorizontalDivider(color = AppTheme.colors.backgroundSecondary, thickness = 0.5.dp)
      VSpacer(8.dp)

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        if (item.humidity != null) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = stringResource(R.string.weather_humidity),
              style = AppTheme.typography.labelMedium,
              color = AppTheme.colors.contentSecondary,
            )
            VSpacer(2.dp)
            Text(
              text = "${item.humidity}%",
              style = AppTheme.typography.titleSmall,
              color = accentColor.copy(alpha = 0.8f),
            )
          }
        }

        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
          Text(
            text = stringResource(R.string.weather_wind_speed),
            style = AppTheme.typography.labelMedium,
            color = AppTheme.colors.contentSecondary,
          )
          VSpacer(2.dp)
          Text(
            text = "${item.windSpeed.toInt()} ${stringResource(R.string.kilometers_per_hour)}",
            style = AppTheme.typography.titleSmall,
            color = accentColor.copy(alpha = 0.8f),
          )
        }
      }

      // Precipitation Detail
      if (item.precipitation > 0) {
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
              text = "${item.precipitation.toInt()} ${stringResource(R.string.millimeter)}",
              style = AppTheme.typography.titleSmall,
              color = accentColor,
            )
          }
        }
      }
    }
  }
}

@Composable
private fun getPrecipitationStatus(weatherCodeRef: Int): ExpandableHourlyCard.PrecipitationStatus =
  ExpandableHourlyCard.getPrecipitationStatus(weatherCodeRef)
