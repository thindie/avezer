package com.thindie.avezer.feature.search

import com.thindie.avezer.HomeSection
import com.thindie.avezer.engine.RouteFactory
import com.thindie.avezer.engine.ScreenScope
import com.thindie.avezer.feature.home.HomeFlow
import com.thindie.avezer.feature.home.data.di.AppFlowModule
import com.thindie.avezer.feature.home.search.SearchScreenCommand
import com.thindie.avezer.feature.home.search.SearchScreenState

object SearchRoute {
  fun create(flowModule: AppFlowModule) =
    RouteFactory.create(
      id = "search_places",
      initialState = SearchScreenState(),
      execute = { cmd: SearchScreenCommand, state ->
        val scope = ScreenScope<SearchScreenState, SearchScreenCommand>()
        SearchFlow.exec(scope, cmd, state)
      },
      stateSink = { scope: ScreenScope<SearchScreenState, SearchScreenCommand> ->
        scope.subscriptions(flowModule.placesRepository)
      },
      errorMapper = com.thindie.avezer.feature.home.search.searchScreenErrorMapper(),
      routeContent = { scope -> com.thindie.avezer.feature.home.search.SearchScreen(scope) },
      section = HomeSection.PlacesSearch,
    )
}
