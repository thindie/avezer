package com.thindie.avezer.widget.data

/**
 * Holder for the WeatherDataProvider instance shared across widget module.
 */
object WeatherDataProviderHolder {
  @Volatile
  private var dataProvider: WeatherDataProvider? = null

  fun getDataProvider(): WeatherDataProvider? = dataProvider

  fun setDataProvider(provider: WeatherDataProvider) {
    dataProvider = provider
  }
}
