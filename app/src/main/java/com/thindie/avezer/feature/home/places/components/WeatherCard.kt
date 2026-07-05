package com.thindie.avezer.feature.home.places.components

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
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.thindie.avezer.R
import com.thindie.avezer.feature.home.domain.TimeFormatter
import com.thindie.avezer.feature.home.domain.Weather
import com.thindie.avezer.uikit.AppTheme
import com.thindie.avezer.uikit.VSpacer
import com.thindie.avezer.uikit.weather.WeatherColorMapper

@Composable
internal fun WeatherCard(
  weather: Weather,
  modifier: Modifier = Modifier,
  onClick: () -> Unit,
) {
  val accentColor = WeatherColorMapper.getAccentColor(weather.weatherCodeRef)

  Card(
    modifier =
      modifier
        .padding(horizontal = 8.dp, vertical = 4.dp)
        .clickable(onClick = onClick),
    colors = CardDefaults.cardColors(containerColor = AppTheme.colors.backgroundSecondary),
    shape = RoundedCornerShape(16.dp),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
  ) {
    Column(
      modifier =
        Modifier
          .padding(16.dp)
          .fillMaxWidth()
          .semantics { contentDescription = "${weather.city} weather card" },
    ) {
      // Header: City name and Weather Icon
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
          text = weather.city,
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
            text = weather.emoji,
            style = AppTheme.typography.headlineSmall,
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
            text = "${weather.temperature.toInt()}°",
            style = AppTheme.typography.headlineSmall,
            color = accentColor,
          )
        }

        // Precipitation Status
        val precipitationStatus = getPrecipitationStatus(weather.weatherCodeRef)
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

      // Precipitation Details (if applicable)
      if (weather.humidity != null || weather.windSpeed != null) {
        VSpacer(12.dp)
        HorizontalDivider(color = AppTheme.colors.backgroundPrimary, thickness = 0.5.dp)
        VSpacer(8.dp)

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          if (weather.humidity != null) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = stringResource(R.string.weather_humidity),
                style = AppTheme.typography.labelMedium,
                color = AppTheme.colors.contentSecondary,
              )
              VSpacer(2.dp)
              Text(
                text = "${weather.humidity}%",
                style = AppTheme.typography.titleSmall,
                color = accentColor.copy(alpha = 0.8f),
              )
            }
          }

          if (weather.windSpeed != null) {
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
              Text(
                text = stringResource(R.string.weather_wind_speed),
                style = AppTheme.typography.labelMedium,
                color = AppTheme.colors.contentSecondary,
              )
              VSpacer(2.dp)
              Text(
                text = "${weather.windSpeed.toInt()} ${stringResource(R.string.kilometers_per_hour)}",
                style = AppTheme.typography.titleSmall,
                color = accentColor.copy(alpha = 0.8f),
              )
            }
          }
        }
      }

      // Last Updated Section
      VSpacer(12.dp)
      HorizontalDivider(color = AppTheme.colors.backgroundPrimary, thickness = 0.5.dp)
      VSpacer(8.dp)

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
          text = stringResource(R.string.last_updated_label),
          style = AppTheme.typography.labelMedium,
          color = AppTheme.colors.contentSecondary,
        )

        val lastUpdatedTime = TimeFormatter.formatRelativeTime(weather.lastUpdated)

        Text(
          text = lastUpdatedTime,
          style = AppTheme.typography.titleSmall,
          color = accentColor.copy(alpha = 0.8f),
        )
      }
    }
  }
}

@Immutable
private data class PrecipitationStatus(
  val emoji: String,
  val labelResId: Int,
)

@Composable
private fun getPrecipitationStatus(weatherCodeRef: Int): PrecipitationStatus {
  return when (weatherCodeRef) {
    // Rain codes (51-67, 80-82)
    in 51..67, in 80..82 -> PrecipitationStatus(emoji = "🌧️", labelResId = R.string.weather_condition_rain)
    // Snow codes (71-77, 85-86)
    in 71..77, in 85..86 -> PrecipitationStatus(emoji = "❄️", labelResId = R.string.weather_condition_snow)
    // Thunderstorm codes (95-99)
    in 95..99 -> PrecipitationStatus(emoji = "⛈️", labelResId = R.string.weather_condition_rain)
    // Clear/Cloudy/Fog (0-3, 45-48)
    else -> PrecipitationStatus(emoji = "☀️", labelResId = R.string.weather_condition_clear)
  }
}
