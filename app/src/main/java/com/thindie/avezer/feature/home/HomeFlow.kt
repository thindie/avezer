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
  private var deeplink: Deeplink = Deeplink.NotSpecified

  fun set(deeplink: Deeplink): HomeFlow {
    this.deeplink = deeplink
    return this
  }

  override fun start() {
    when (val deeplink = consumeDeeplink()) {
      is Deeplink.Details -> router.push(placeDetail(deeplink.weather))
      is Deeplink.NotSpecified -> router.push(places)
    }
  }

  fun switch(screen: Start? = null) {
    when (screen) {
      is Start.Details -> router.replaceTop(placeDetail(screen.weather))
      null -> router.replaceTop(places)
    }
  }

  private fun consumeDeeplink(): Deeplink {
    val deepl = this@HomeFlow.deeplink
    deeplink = Deeplink.NotSpecified
    return deepl
  }

  sealed interface Result {
    data object Search : Result

    data object Settings : Result
  }

  sealed interface Start {
    data class Details(val weather: Weather) : Start
  }

  sealed interface Deeplink {
    data class Details(val weather: Weather) : Deeplink

    data object NotSpecified : Deeplink
  }
}
