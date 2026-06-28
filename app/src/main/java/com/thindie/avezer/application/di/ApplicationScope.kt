package com.thindie.avezer.application.di

import android.content.Context
import com.thindie.avezer.application.Application
import com.thindie.avezer.application.clientImpl.ClientImpl
import com.thindie.avezer.application.storage.LocationResolverImpl
import com.thindie.avezer.application.storage.StorageImpl
import com.thindie.avezer.engine.Log
import com.thindie.avezer.feature.home.data.di.AppFlowModule
import com.thindie.avezer.feature.settings.data.SettingsRepositoryImpl
import com.thindie.avezer.feature.settings.domain.SettingsRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class ApplicationScope(private val application: Application) {
  private val appScopeStore = "appScopeStore"
  private val prefs by lazy { application.getSharedPreferences(appScopeStore, Context.MODE_PRIVATE) }
  private val baseUrl = "https://api.open-meteo.com/"
  private val storage by lazy { StorageImpl(prefs) }
  val scope =
    CoroutineScope(
      SupervisorJob() +
        Dispatchers.IO +
        CoroutineExceptionHandler { _, throwable ->
          Log.e({ "ApplicationScope unhandled exception" }, throwable = throwable)
        },
    )

  val appFlowModule =
    AppFlowModule(
      storage = storage,
      client = ClientImpl(baseUrl),
      resolver = LocationResolverImpl(application),
      appCoroutineScope = scope,
    )

  val settingsRepository: SettingsRepository by lazy {
    SettingsRepositoryImpl(storage = storage, scope = scope)
  }
}
