package com.thindie.avezer.feature.home

import com.thindie.avezer.engine.Route
import com.thindie.avezer.engine.Router
import com.thindie.avezer.engine.ScreenFlow
import com.thindie.avezer.feature.home.data.di.AppFlowModule
import com.thindie.avezer.feature.home.domain.Weather
import com.thindie.avezer.feature.home.placedetail.placeDetail
import com.thindie.avezer.feature.home.places.places

class HomeFlow(
  private val router: Router,
  val flowModule: AppFlowModule,
) : ScreenFlow<Route, HomeFlow.Result>(router) {
  override fun start() {
    router.push(places)
  }

  fun switch(screen: Start? = null) {
    when (screen) {
      is Start.Details -> router.replaceTop(placeDetail(screen.weather))
      null -> router.replaceTop(places)
    }
  }

  sealed interface Result {
    data object Search : Result

    data object Settings : Result
  }

  sealed interface Start {
    data class Details(val weather: Weather) : Start
  }
}
