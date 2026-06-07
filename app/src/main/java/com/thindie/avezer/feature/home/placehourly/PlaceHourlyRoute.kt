package com.thindie.avezer.feature.home.placehourly

import com.thindie.avezer.engine.RouteFactory
import com.thindie.avezer.engine.ScreenScope
import com.thindie.avezer.feature.home.HomeFlow
import com.thindie.avezer.feature.home.domain.Weather

fun HomeFlow.placeHourly(weather: Weather) =
  RouteFactory.create(
    initialState = PlaceHourlyState(),
    execute = { cmd, state -> exec(cmd, state) },
    stateSink = { screenScope: ScreenScope<PlaceHourlyState, PlaceHourlyCommand> ->
      screenScope.subscriptions(flowModule.repository, weather)
    },
    errorMapper = placeHourlyScreenErrorMapper(),
    routeContent = { PlaceHourlyScreen() },
  )
