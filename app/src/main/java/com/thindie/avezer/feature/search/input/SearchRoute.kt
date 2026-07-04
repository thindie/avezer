package com.thindie.avezer.feature.search.input

import com.thindie.avezer.HomeSection
import com.thindie.avezer.engine.RouteFactory
import com.thindie.avezer.engine.ScreenScope
import com.thindie.avezer.feature.search.SearchFlow

val SearchFlow.SearchRoute get() =
  RouteFactory.create(
    id = "search_places",
    initialState = SearchScreenState(),
    execute = { cmd: SearchScreenCommand, state ->
      exec(cmd, state)
    },
    stateSink = { scope: ScreenScope<SearchScreenState, SearchScreenCommand> ->
      scope.subscriptions(flowModule.placesRepository)
    },
    errorMapper = searchScreenErrorMapper(),
    routeContent = { scope -> SearchScreen(scope) },
    section = HomeSection.Search,
  )
