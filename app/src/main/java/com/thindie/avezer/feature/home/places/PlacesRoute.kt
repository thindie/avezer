package com.thindie.avezer.feature.home.places

import com.thindie.avezer.engine.RouteFactory
import com.thindie.avezer.engine.ScreenScope
import com.thindie.avezer.feature.home.HomeFlow

@Suppress("EXTENSION_SHOULD_BE_PROPERTY")
val HomeFlow.places
    get() = RouteFactory.create<ScreenCommand, ScreenState>(
        initialState = ScreenState(),
        execute = { cmd, state -> exec(cmd, state, flowModule.repository) },
        stateSink = { screenScope: ScreenScope<ScreenState, ScreenCommand> ->
            screenScope.subscriptions(flowModule.repository)
        },
        initialCommand = RouteFactory.InitialCommand { ScreenCommand.Fetch },
        routeContent = { PlacesScreen() }
    )
