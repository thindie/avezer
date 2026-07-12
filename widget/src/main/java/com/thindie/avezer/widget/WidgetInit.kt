package com.thindie.avezer.widget

import android.content.Context
import com.thindie.avezer.widget.data.WeatherDataProvider
import com.thindie.avezer.widget.data.WeatherDataProviderHolder
import com.thindie.avezer.widget.updater.WidgetUpdateWorker

/**
 * Helper class for initializing the weather widget module.
 */
object WidgetInit {
  suspend fun init(
    context: Context,
    dataProvider: WeatherDataProvider,
  ) {
    // Store the provider reference
    WeatherDataProviderHolder.setDataProvider(dataProvider)

    // Register the Glance app widget with the system
    WeatherWidgetProvider.register(context)

    // Start periodic updates using WorkManager
    WidgetUpdateWorker.startPeriodicUpdates(context)
  }
}
