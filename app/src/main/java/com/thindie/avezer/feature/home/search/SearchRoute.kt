package com.thindie.avezer.feature.home.search

import com.thindie.avezer.engine.RouteFactory
import com.thindie.avezer.engine.ScreenScope
import com.thindie.avezer.feature.home.HomeFlow

val HomeFlow.searchPlaces
  get() =
    RouteFactory.create(
      id = "search_places",
      initialState = SearchScreenState(),
      execute = { cmd, state -> exec(cmd, state) },
      stateSink = { scope: ScreenScope<SearchScreenState, SearchScreenCommand> ->
        scope.subscriptions(flowModule.placesRepository)
      },
      errorMapper = searchScreenErrorMapper(),
      routeContent = { scope -> SearchScreen(scope) },
    )
