package com.thindie.avezer.feature.home.data.di

import com.thindie.avezer.application.LocationResolver
import com.thindie.avezer.application.storage.Storage
import com.thindie.avezer.feature.home.data.MainRepositoryImpl
import com.thindie.avezer.feature.home.data.PlacesRepositoryImpl
import com.thindie.avezer.feature.home.domain.MainRepository
import com.thindie.avezer.feature.home.domain.PlacesRepository
import com.thindie.avezer.network.Client
import kotlinx.coroutines.CoroutineScope

class AppFlowModule(
  private val client: Client,
  private val storage: Storage,
  private val resolver: LocationResolver,
  private val appCoroutineScope: CoroutineScope,
) {
  private val _repository by lazy {
    MainRepositoryImpl(
      locationResolver = resolver,
      storage = storage,
      client = client,
    )
  }

  val repository: MainRepository get() = _repository

  private val _placesRepository by lazy {
    PlacesRepositoryImpl(storage, appCoroutineScope, resolver)
  }

  val placesRepository: PlacesRepository get() = _placesRepository
}
