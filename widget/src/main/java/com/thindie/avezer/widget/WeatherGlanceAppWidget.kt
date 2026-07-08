package com.thindie.avezer.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.thindie.avezer.widget.data.WeatherDataProviderHolder
import com.thindie.avezer.widget.data.WeatherWidgetData
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Main Glance App Widget for displaying weather information.
 */
class WeatherGlanceAppWidget : GlanceAppWidget() {
  override suspend fun provideGlance(
    context: Context,
    id: GlanceId,
  ) {
    val dataProvider = WeatherDataProviderHolder.getDataProvider() ?: return
    val weatherData =
      withContext(Dispatchers.Default) {
        try {
          dataProvider.fetch()
          dataProvider.getWidgetData()
        } catch (e: Exception) {
          when (e) {
            is CancellationException -> throw e
            else -> WeatherWidgetData.Error
          }
        }
      }

    provideContent {
      when (weatherData) {
        WeatherWidgetData.Error -> ErrorWidgetContent()
        is WeatherWidgetData.Forecast -> {
          WeatherWidgetContent(data = weatherData)
        }
        WeatherWidgetData.None -> EmptyWidgetContent()
        WeatherWidgetData.Outdated -> TODO()
      }
    }
  }

  @Composable
  private fun WeatherWidgetContent(data: WeatherWidgetData.Forecast) {
    Scaffold(
      titleBar = {
        Row(
          modifier =
            GlanceModifier
              .padding(horizontal = 8.dp, vertical = 8.dp),
        ) {
          Text(
            text = data.city,
            style =
              TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
              ),
          )
        }
      },
    ) {
      Column {
        Row(
          modifier =
            GlanceModifier
              .padding(horizontal = 8.dp, vertical = 8.dp),
        ) {
          Text(
            text = data.emoji,
            style =
              TextStyle(
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
              ),
          )
          Spacer(modifier = GlanceModifier.width(12.dp))
          Text(
            text = "${data.temperature.toInt()}°",
            style =
              TextStyle(
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
              ),
          )
        }

        if (data.precipitationSum != null) {
          Spacer(modifier = GlanceModifier.height(6.dp))
          Row(
            modifier =
              GlanceModifier
                .padding(horizontal = 8.dp, vertical = 4.dp),
          ) {
            Text(
              text = "\uD83C\uDF26",
              style = TextStyle(fontSize = 16.sp),
            )
            Spacer(modifier = GlanceModifier.width(4.dp))
            Text(
              text = data.precipitationSum,
              style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal),
            )
          }
        } else {
          Text(
            text = glanceStringResource(R.string.widget_precipitation_null),
            style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal),
          )
        }

        if (data.windSpeed != null) {
          Spacer(modifier = GlanceModifier.height(6.dp))
          Row(
            modifier =
              GlanceModifier
                .padding(horizontal = 8.dp, vertical = 4.dp),
          ) {
            Text(
              text = "\uD83C\uDF2C",
              style = TextStyle(fontSize = 16.sp),
            )
            Spacer(modifier = GlanceModifier.width(4.dp))
            Text(
              text = data.windSpeed,
              style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal),
            )
          }
        }

        if (data.lastUpdated.isNotEmpty()) {
          Spacer(modifier = GlanceModifier.height(6.dp))
          Row(
            modifier =
              GlanceModifier
                .padding(horizontal = 8.dp, vertical = 4.dp),
          ) {
            Text(
              text = data.lastUpdated,
              style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Normal),
            )
          }
        }

        Spacer(modifier = GlanceModifier.height(8.dp))
      }
    }
  }

  @Composable
  private fun ErrorWidgetContent() {
    Box(
      modifier =
        GlanceModifier
          .fillMaxSize(),
    ) {
      Column(
        modifier =
          GlanceModifier
            .padding(horizontal = 8.dp, vertical = 16.dp),
      ) {
        Text(
          text = "⚠️",
          style = TextStyle(fontSize = 24.sp),
        )
        Spacer(modifier = GlanceModifier.width(0.dp))
        Text(
          text = glanceStringResource(R.string.widget_error_loading_data),
          style = TextStyle(fontSize = 16.sp),
        )
      }
    }
  }

  @Composable
  private fun EmptyWidgetContent() {
    Scaffold(
      titleBar = {
        Text(
          text = glanceStringResource(R.string.widget_no_data_available),
          style = TextStyle(fontSize = 16.sp),
        )
      },
    ) {
      Column(
        modifier =
          GlanceModifier
            .padding(horizontal = 8.dp, vertical = 16.dp),
      ) {
        Text(
          text = "📡",
          style = TextStyle(fontSize = 24.sp),
        )
        Spacer(modifier = GlanceModifier.width(0.dp))
        Text(
          text = "No data available",
          style = TextStyle(fontSize = 16.sp),
        )
      }
    }
  }
}

@Composable
internal fun glanceStringResource(res: Int): String {
  return LocalContext.current.getString(res)
}
