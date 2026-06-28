package com.thindie.avezer.feature.settings

import android.content.Context
import com.thindie.avezer.engine.Route
import com.thindie.avezer.engine.Router
import com.thindie.avezer.engine.ScreenFlow
import com.thindie.avezer.feature.settings.domain.SettingsRepository
import com.thindie.avezer.feature.settings.selection.selection

class SettingsFlow(
  private val router: Router,
  val repository: SettingsRepository,
  val context: Context,
) : ScreenFlow<Route, Unit>(router) {
  override fun start() {
    router.push(selection)
  }

  fun switch() {
    router.replaceTop(selection)
  }
}
