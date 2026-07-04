package com.thindie.avezer.feature.home.data.di

import com.thindie.avezer.application.LocationResolver
import com.thindie.avezer.application.storage.Storage
import com.thindie.avezer.feature.home.data.ForecastRepositoryImpl
import com.thindie.avezer.feature.home.data.PlacesRepositoryImpl
import com.thindie.avezer.feature.home.domain.ForecastRepository
import com.thindie.avezer.feature.home.domain.PlacesRepository
import com.thindie.avezer.network.Client
import kotlinx.coroutines.CoroutineScope

class AppFlowModule(
  private val client: Client,
  val storage: Storage,
  private val resolver: LocationResolver,
  private val appCoroutineScope: CoroutineScope,
) {
  private val _repository by lazy {
    ForecastRepositoryImpl(
      locationResolver = resolver,
      storage = storage,
      client = client,
    )
  }

  val repository: ForecastRepository get() = _repository

  private val _placesRepository by lazy {
    PlacesRepositoryImpl(storage, appCoroutineScope, resolver)
  }

  val placesRepository: PlacesRepository get() = _placesRepository
}
