package com.thindie.avezer.widget.data

/**
 * Interface for providing weather data to the widget.
 * This abstraction allows the widget module to be independent from the main app's repository structure.
 */
interface WeatherDataProvider {
  suspend fun fetch()

  suspend fun getWidgetData(): WeatherWidgetData

  val interaction: WidgetInteraction
}
