package com.thindie.avezer.application

import android.app.Application
import com.thindie.avezer.application.di.ApplicationScope
import com.thindie.avezer.engine.Router
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
      AppStrings.init(this@Application)
    }
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
}
