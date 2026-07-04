package com.thindie.avezer.feature.home.domain

import kotlinx.coroutines.flow.Flow

interface ForecastRepository {
  val forecast: Flow<List<Weather>?>

  suspend fun fetch()

  suspend fun read(
    lat: Double,
    lon: Double,
    cityName: String,
  ): Weather
}
