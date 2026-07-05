package com.thindie.avezer.feature.home.data.di

import com.thindie.avezer.application.LocationResolver
import com.thindie.avezer.application.storage.Storage
import com.thindie.avezer.feature.home.data.ForecastRepositoryImpl
import com.thindie.avezer.feature.home.data.SearchRepositoryImpl
import com.thindie.avezer.feature.home.domain.ForecastRepository
import com.thindie.avezer.feature.home.domain.SearchRepository
import com.thindie.avezer.network.Client

class AppFlowModule(
  private val client: Client,
  val storage: Storage,
  private val resolver: LocationResolver,
) {
  private val _repository by lazy {
    ForecastRepositoryImpl(
      storage = storage,
      client = client,
    )
  }

  val repository: ForecastRepository get() = _repository

  private val placesRepositoryLazy by lazy {
    SearchRepositoryImpl(storage, resolver)
  }

  val searchRepository: SearchRepository get() = placesRepositoryLazy
}
