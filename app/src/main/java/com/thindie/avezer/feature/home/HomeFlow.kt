package com.thindie.avezer.feature.home

import com.thindie.avezer.engine.Route
import com.thindie.avezer.engine.Router
import com.thindie.avezer.engine.ScreenFlow
import com.thindie.avezer.feature.home.data.di.AppFlowModule
import com.thindie.avezer.feature.home.places.places

class HomeFlow(
  private val router: Router,
  val flowModule: AppFlowModule,
) : ScreenFlow<Route, Unit>(router) {
  override fun start() {
    router.push(places)
  }
}
