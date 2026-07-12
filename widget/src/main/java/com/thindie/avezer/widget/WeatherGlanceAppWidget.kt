package com.thindie.avezer.widget

import android.content.Context
import androidx.compose.runtime.rememberCoroutineScope
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import com.thindie.avezer.widget.components.EmptyWidgetContent
import com.thindie.avezer.widget.components.ErrorWidgetContent
import com.thindie.avezer.widget.components.GlanceTheme
import com.thindie.avezer.widget.components.WeatherWidgetContent
import com.thindie.avezer.widget.data.WeatherDataProviderHolder
import com.thindie.avezer.widget.data.WeatherWidgetData
import com.thindie.avezer.widget.data.WidgetState
import com.thindie.avezer.widget.updater.WidgetUpdateWorker
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
      val scope = rememberCoroutineScope()
      GlanceTheme {
        when (weatherData) {
          WeatherWidgetData.Error ->
            ErrorWidgetContent {
              dataProvider.interaction.invoke(WidgetState.Error)
            }
          is WeatherWidgetData.Forecast -> {
            WeatherWidgetContent(
              data = weatherData,
              onClick = {
                dataProvider.interaction.invoke(WidgetState.Ok)
              },
              onUpdate = { scope.launch { WidgetUpdateWorker.updateAllWidgets(context) } },
            )
          }

          WeatherWidgetData.None ->
            EmptyWidgetContent {
              dataProvider.interaction.invoke(WidgetState.Error)
            }
          WeatherWidgetData.Outdated ->
            ErrorWidgetContent {
              dataProvider.interaction.invoke(WidgetState.Error)
            }
        }
      }
    }
  }
}
