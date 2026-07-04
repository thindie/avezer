package com.thindie.avezer.feature.search

import com.thindie.avezer.engine.Route
import com.thindie.avezer.engine.Router
import com.thindie.avezer.engine.ScreenFlow
import com.thindie.avezer.feature.home.data.di.AppFlowModule
import com.thindie.avezer.feature.home.domain.Weather
import com.thindie.avezer.feature.search.input.SearchRoute

class SearchFlow(
  private val router: Router,
  val flowModule: AppFlowModule,
) : ScreenFlow<Route, SearchFlow.Result>(router) {
  override fun start() {
    router.push(SearchRoute)
  }

  fun switch() {
    router.replaceTop(SearchRoute)
  }

  sealed interface Result {
    data class PlaceRequested(val result: Weather) : Result
  }
}
