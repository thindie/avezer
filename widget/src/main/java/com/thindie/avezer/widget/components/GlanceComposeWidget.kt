package com.thindie.avezer.widget.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.Text
import com.thindie.avezer.widget.R
import com.thindie.avezer.widget.data.WeatherWidgetData

@Composable
internal fun WeatherWidgetContent(
  data: WeatherWidgetData.Forecast,
  onClick: () -> Unit,
  onUpdate: () -> Unit,
) {
  Scaffold(
    modifier = GlanceModifier.clickable(onClick),
    horizontalPadding = 16.dp,
    backgroundColor = GlanceTheme.colors.cardPrimary,
    titleBar = {
      Row {
        Text(
          modifier = GlanceModifier.padding(start = 24.dp, top = 8.dp),
          text = data.city,
          style = GlanceTheme.typography.titleLarge(GlanceTheme.colors.contentPrimary),
        )
      }
    },
  ) {
    Column {
      Spacer(modifier = GlanceModifier.height(16.dp))
      Row(
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Text(
          text = data.emoji,
          style = GlanceTheme.typography.headlineMedium(GlanceTheme.colors.weatherSunny),
        )
        Spacer(modifier = GlanceModifier.width(16.dp))
        Text(
          text = "${data.temperature.toInt()}°",
          style = GlanceTheme.typography.headlineLarge(GlanceTheme.colors.contentPrimary),
        )
      }

      if (data.precipitationSum != null) {
        Spacer(modifier = GlanceModifier.height(8.dp))
        Row(
          modifier =
            GlanceModifier
              .padding(horizontal = 16.dp, vertical = 4.dp),
        ) {
          Text(
            text = glanceStringResource(R.string.widget_precipitation_expected),
            style = GlanceTheme.typography.labelSmall(GlanceTheme.colors.contentTertiary),
          )
          Spacer(modifier = GlanceModifier.width(4.dp))
          Text(
            text = data.precipitationSum,
            style = GlanceTheme.typography.bodyLarge(GlanceTheme.colors.contentPrimary),
          )
        }
      } else {
        Text(
          text = glanceStringResource(R.string.widget_precipitation_null),
          style = GlanceTheme.typography.labelSmall(GlanceTheme.colors.contentTertiary),
        )
      }

      if (data.windSpeed != null) {
        Spacer(modifier = GlanceModifier.height(8.dp))
        Row(
          modifier =
            GlanceModifier
              .padding(horizontal = 16.dp, vertical = 4.dp),
        ) {
          Text(
            text = "\uD83C\uDF2C",
            style = GlanceTheme.typography.titleSmall(GlanceTheme.colors.accentPrimary),
          )
          Spacer(modifier = GlanceModifier.width(4.dp))
          Text(
            text = data.windSpeed,
            style = GlanceTheme.typography.bodyLarge(GlanceTheme.colors.contentPrimary),
          )
        }
      }

      if (data.lastUpdated.isNotEmpty()) {
        Spacer(modifier = GlanceModifier.height(8.dp))
        Row(
          modifier =
            GlanceModifier
              .padding(horizontal = 16.dp, vertical = 4.dp),
        ) {
          Text(
            text = glanceStringResource(R.string.widget_updated_label),
            style = GlanceTheme.typography.labelSmall(GlanceTheme.colors.contentTertiary),
          )
          Spacer(modifier = GlanceModifier.width(4.dp))
          Text(
            text = data.lastUpdated,
            style = GlanceTheme.typography.bodyMedium(GlanceTheme.colors.contentSecondary),
          )
          Spacer(GlanceModifier.width(16.dp))
          Image(
            modifier =
              GlanceModifier
                .clickable({ onUpdate() })
                .size(24.dp),
            provider = ImageProvider(R.drawable.ic_refresh_16),
            contentDescription = null,
            colorFilter = ColorFilter.tint(GlanceTheme.colors.accentPrimary),
          )
        }
      }

      // Temperature bar chart for daily forecast
      if (data.dailyTempsMax.isNotEmpty()) {
        Spacer(modifier = GlanceModifier.height(8.dp))
        DailyTempBarChart(
          maxTemps = data.dailyTempsMax,
          minTemps = data.dailyTempsMin,
          currentDayIndex = data.currentDayIndex,
        )
      }

      Spacer(modifier = GlanceModifier.height(16.dp))
    }
  }
}

@Composable
private fun DailyTempBarChart(
  maxTemps: List<Double>,
  minTemps: List<Double>,
  currentDayIndex: Int,
) {
  // Find global min/max for scaling bar heights
  val allTemps = (maxTemps + minTemps).filter { it.isFinite() }
  if (allTemps.isEmpty()) return
  val globalMin = allTemps.min()
  val globalMax = allTemps.max()
  val range = if (globalMax - globalMin == 0.0) 1.0 else (globalMax - globalMin)

  Row(
    modifier =
      GlanceModifier
        .padding(horizontal = 8.dp, vertical = 4.dp),
  ) {
    maxTemps.forEachIndexed { index, temp ->
      val heightFraction = (temp - globalMin) / range
      val barHeightDp = (heightFraction * 48).coerceIn(4.0, 48.0)
      Spacer(
        modifier =
          GlanceModifier
            .width(12.dp)
            .height(barHeightDp.dp),
      )
    }
  }
}

@Composable
internal fun ErrorWidgetContent() {
  Scaffold {
    Box(
      modifier =
        GlanceModifier
          .fillMaxSize(),
    ) {
      Column(
        modifier =
          GlanceModifier
            .padding(horizontal = 16.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Text(
          text = "⚠️",
          style = GlanceTheme.typography.headlineLarge(GlanceTheme.colors.errorPrimary),
        )
        Spacer(modifier = GlanceModifier.height(12.dp))
        Text(
          text = glanceStringResource(R.string.widget_error_loading_data),
          style = GlanceTheme.typography.bodyMedium(GlanceTheme.colors.contentPrimary),
        )
      }
    }
  }
}

@Composable
internal fun EmptyWidgetContent() {
  Scaffold(
    titleBar = {
      Text(
        text = glanceStringResource(R.string.widget_no_data_available),
        style = GlanceTheme.typography.bodyLarge(GlanceTheme.colors.contentPrimary),
      )
    },
  ) {
    Column(
      modifier =
        GlanceModifier
          .padding(horizontal = 16.dp, vertical = 24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Text(
        text = "📡",
        style = GlanceTheme.typography.headlineLarge(GlanceTheme.colors.accentPrimary),
      )
      Spacer(modifier = GlanceModifier.height(12.dp))
      Text(
        text = glanceStringResource(R.string.widget_no_data_message),
        style = GlanceTheme.typography.bodyMedium(GlanceTheme.colors.contentTertiary),
      )
    }
  }
}

@Composable
private fun glanceStringResource(res: Int): String {
  return LocalContext.current.getString(res)
}
