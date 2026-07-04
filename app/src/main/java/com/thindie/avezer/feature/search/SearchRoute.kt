package com.thindie.avezer.feature.search

import com.thindie.avezer.HomeSection
import com.thindie.avezer.engine.RouteFactory
import com.thindie.avezer.engine.ScreenScope
import com.thindie.avezer.feature.home.search.SearchScreenCommand
import com.thindie.avezer.feature.home.search.SearchScreenState

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
    errorMapper = com.thindie.avezer.feature.home.search.searchScreenErrorMapper(),
    routeContent = { scope -> com.thindie.avezer.feature.home.search.SearchScreen(scope) },
    section = HomeSection.Search,
  )
