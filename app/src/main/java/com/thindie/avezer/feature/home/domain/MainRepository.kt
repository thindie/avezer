package com.thindie.avezer.feature.home.domain

import kotlinx.coroutines.flow.Flow

interface MainRepository {
  val forecast: Flow<List<Weather>?>

  suspend fun fetch()

  suspend fun read(cityName: String)

  suspend fun read(
    lat: Double,
    lon: Double,
    cityName: String,
  ): Weather
}
