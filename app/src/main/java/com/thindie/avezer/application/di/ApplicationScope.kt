package com.thindie.avezer.application.di

import android.content.Context
import com.thindie.avezer.application.Application
import com.thindie.avezer.application.clientImpl.ClientImpl
import com.thindie.avezer.application.storage.LocationResolverImpl
import com.thindie.avezer.application.storage.StorageImpl
import com.thindie.avezer.feature.home.data.di.AppFlowModule

class ApplicationScope(private val application: Application) {
  private val appScopeStore = "appScopeStore"
  private val prefs by lazy { application.getSharedPreferences(appScopeStore, Context.MODE_PRIVATE) }
  private val baseUrl = "https://api.open-meteo.com/"

  val appFlowModule =
    AppFlowModule(
      storage = StorageImpl(prefs),
      client = ClientImpl(baseUrl),
      resolver = LocationResolverImpl(application),
    )
}
