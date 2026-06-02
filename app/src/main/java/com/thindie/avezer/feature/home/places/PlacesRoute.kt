package com.thindie.avezer.feature.home.places

import com.thindie.avezer.engine.RouteFactory
import com.thindie.avezer.engine.ScreenScope
import com.thindie.avezer.feature.home.HomeFlow

val HomeFlow.places
    get() = RouteFactory.create (
        initialState = ScreenState(),
        execute = { cmd, state -> exec(cmd, state, flowModule.repository) },
        stateSink = { screenScope: ScreenScope<ScreenState, ScreenCommand> ->
            screenScope.subscriptions(flowModule.repository)
        },
        errorMapper = placesScreenErrorMapper(),
        initialCommand = { ScreenCommand.Fetch },
        routeContent = { PlacesScreen() }
    )
