package com.thindie.avezer.widget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver

/**
 * Glance App Widget Receiver for weather widget.
 * Handles widget updates and initialization.
 */
class WeatherWidgetProvider : GlanceAppWidgetReceiver() {
  override val glanceAppWidget: GlanceAppWidget = WeatherGlanceAppWidget()

  companion object {
    /**
     * Updates all existing Glance app widgets.
     * The widget appears in the system picker via <receiver> declaration in AndroidManifest.xml.
     */
    suspend fun register(context: Context) {
      val manager = GlanceAppWidgetManager(context)

      // Update any existing widgets
      val glanceIds = manager.getGlanceIds(WeatherGlanceAppWidget::class.java)
      for (glanceId in glanceIds) {
        WeatherGlanceAppWidget().update(context, glanceId)
      }
    }
  }
}
