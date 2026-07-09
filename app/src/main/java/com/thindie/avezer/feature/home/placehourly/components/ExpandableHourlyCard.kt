package com.thindie.avezer.feature.home.placehourly.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.thindie.avezer.R
import com.thindie.avezer.feature.home.domain.HourlyForecastItem
import com.thindie.avezer.feature.home.domain.TimeFormatter
import com.thindie.avezer.uikit.AppTheme
import com.thindie.avezer.uikit.VSpacer
import com.thindie.avezer.uikit.weather.WeatherColorMapper

@Composable
internal fun ExpandableHourlyCard(
  item: HourlyForecastItem,
  isExpanded: Boolean,
  onToggle: () -> Unit,
  modifier: Modifier = Modifier,
  isCurrentHour: Boolean = false,
  isPassedHour: Boolean = false,
) {
  val accentColor = WeatherColorMapper.getAccentColor(item.weatherCodeRef)

  val temperatureText =
    when {
      isCurrentHour -> stringResource(R.string.weather_temperature_now)
      isPassedHour -> stringResource(R.string.weather_recorded_temperature)
      else -> stringResource(R.string.weather_temperature_forecast)
    }

  Card(
    modifier =
      modifier
        .padding(horizontal = 8.dp, vertical = 4.dp)
        .clickable(onClick = onToggle),
    colors = CardDefaults.cardColors(containerColor = AppTheme.colors.cardPrimary),
    shape = RoundedCornerShape(16.dp),
    border = if (isCurrentHour) BorderStroke(width = 2.dp, color = AppTheme.colors.accentPrimary) else null,
    elevation = CardDefaults.cardElevation(defaultElevation = if (isCurrentHour) 8.dp else 4.dp),
  ) {
    Column(
      modifier =
        Modifier
          .padding(16.dp)
          .fillMaxWidth(),
    ) {
      // Header: Time, Weather Icon and Arrow
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

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          Text(
            text = item.emoji,
            style = AppTheme.typography.headlineSmall,
          )
          val degrees by animateFloatAsState(if (isExpanded) 180f else 0f)
          Text(
            modifier = Modifier.rotate(degrees),
            text = "▼",
            style = AppTheme.typography.bodyMedium,
            color = AppTheme.colors.contentSecondary,
          )
        }
      }

      VSpacer(12.dp)

      // Compact: Temperature and Precipitation
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = temperatureText,
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
      }

      // Expanded: Humidity and Wind Speed
      AnimatedVisibility(visible = isExpanded) {
        Column(modifier = Modifier.fillMaxWidth()) {
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
  }
}
