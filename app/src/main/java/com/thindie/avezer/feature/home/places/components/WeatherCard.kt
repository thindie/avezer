package com.thindie.avezer.feature.home.places.components

import androidx.compose.foundation.clickable
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
import com.thindie.avezer.R
import com.thindie.avezer.feature.home.domain.Weather
import com.thindie.avezer.uikit.AppTheme
import com.thindie.avezer.uikit.Toggle
import com.thindie.avezer.uikit.VSpacer

@Composable
internal fun WeatherCard(
  weather: Weather,
  modifier: Modifier = Modifier,
  onClick: () -> Unit,
) {
  Card(
    modifier = modifier.padding(8.dp).clickable { onClick() },
    colors = CardDefaults.cardColors(containerColor = AppTheme.colors.backgroundSecondary),
    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
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
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = weather.city,
            style = AppTheme.typography.headlineSmall,
            color = AppTheme.colors.contentPrimary,
          )

          VSpacer(2.dp)

          val latStr = "%.2f".format(weather.lat).takeWhile { it != '.' }.padEnd(6, ' ')
          val lonStr = "%.2f".format(weather.lon).takeWhile { it != '.' }.padEnd(6, ' ')
          Text(
            text = stringResource(R.string.latitude_label, latStr),
            style = AppTheme.typography.bodySmall,
            color = AppTheme.colors.contentSecondary.copy(alpha = 0.7f),
          )
          Text(
            text = stringResource(R.string.longitude_label, lonStr),
            style = AppTheme.typography.bodySmall,
            color = AppTheme.colors.contentSecondary.copy(alpha = 0.7f),
          )
        }

        Column(horizontalAlignment = Alignment.End) {
          Toggle(checked = weather.isDay)
          VSpacer(2.dp)
          Text(
            text = stringResource(R.string.remember_button),
            style = AppTheme.typography.labelMedium,
            color = AppTheme.colors.contentSecondary.copy(alpha = 0.7f),
          )
        }
      }
    }
  }
}
