package com.thindie.avezer.feature.home.placehourly

import com.thindie.avezer.engine.RouteFactory
import com.thindie.avezer.engine.ScreenScope
import com.thindie.avezer.feature.home.HomeFlow
import com.thindie.avezer.feature.home.domain.Weather

fun HomeFlow.placeHourly(
  weather: Weather,
  dailyWeatherIndex: Int,
  hourlyStartIndex: Int,
  hourlyEndIndex: Int,
) = RouteFactory.create(
  id = "place_hourly",
  initialState =
    PlaceHourlyState(
      dailyWeatherIndex = dailyWeatherIndex,
      hourlyStartIndex = hourlyStartIndex,
      hourlyEndIndex = hourlyEndIndex,
      dailyForecast = weather.forecast[dailyWeatherIndex],
    ),
  execute = { cmd, state -> exec(cmd, state) },
  stateSink = { screenScope: ScreenScope<PlaceHourlyState, PlaceHourlyCommand> ->
    screenScope.subscriptions(flowModule.repository, weather)
  },
  errorMapper = placeHourlyScreenErrorMapper(),
  routeContent = { scope -> PlaceHourlyScreen(scope) },
)
