package com.thindie.avezer.feature.home.placedetail

import com.thindie.avezer.engine.RouteFactory
import com.thindie.avezer.engine.ScreenScope
import com.thindie.avezer.feature.home.HomeFlow
import com.thindie.avezer.feature.home.domain.Weather

fun HomeFlow.placeDetail(weather: Weather) =
  RouteFactory.create(
    id = "place_detail",
    initialState = PlaceDetailState(),
    execute = { cmd, state -> exec(cmd, state) },
    stateSink = { screenScope: ScreenScope<PlaceDetailState, PlaceDetailCommand> ->
      screenScope.subscriptions(flowModule.repository, weather)
    },
    initialCommand = { PlaceDetailCommand.Init(weather) },
    errorMapper = placeDetailScreenErrorMapper(),
    routeContent = { scope -> PlaceDetailScreen(scope) },
  )
