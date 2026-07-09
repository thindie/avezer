package com.thindie.avezer.application

import android.app.Application
import android.content.Intent
import com.thindie.avezer.MainActivity
import com.thindie.avezer.application.di.ApplicationScope
import com.thindie.avezer.engine.Router
import com.thindie.avezer.widget.WidgetDataProviderImpl
import com.thindie.avezer.widget.WidgetInit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class Application : Application() {
  lateinit var applicationScope: ApplicationScope
  private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

  private var router: Router? = null

  val finishCommand =
    MutableSharedFlow<Unit>(
      replay = 0,
      extraBufferCapacity = 3,
      BufferOverflow.DROP_LATEST,
    )

  override fun onCreate() {
    super.onCreate()
    applicationScope = ApplicationScope(this)
    scope.launch {
      configureAppWidgets()
    }
    scope.launch {
      AppStrings.init(this@Application)
    }
  }

  private suspend fun configureAppWidgets() {
    val dataProvider =
      WidgetDataProviderImpl(
        context = this@Application,
        forecastRepository = applicationScope.appFlowModule.repository,
        searchRepository = applicationScope.appFlowModule.searchRepository,
        interaction = {
          val intent =
            Intent(this, MainActivity::class.java).apply {
              putExtra(DEEPLINK, DAILY_FORECAST)
              addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

          startActivity(intent)
        },
      )
    WidgetInit.init(this, dataProvider)
  }

  fun requireRouter(): Router {
    if (router == null) {
      router =
        Router {
          finishCommand.tryEmit(Unit)
          router = null
        }
    }
    return requireNotNull(router)
  }

  companion object {
    const val DEEPLINK = "deeplink"
    const val DAILY_FORECAST = "daily_forecast"
  }
}
