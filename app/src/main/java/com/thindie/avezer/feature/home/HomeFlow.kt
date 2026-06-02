package com.thindie.avezer.feature.home

import com.thindie.avezer.engine.Route
import com.thindie.avezer.engine.RouteFactory
import com.thindie.avezer.engine.Router
import com.thindie.avezer.engine.ScreenFlow
import com.thindie.avezer.feature.home.data.di.AppFlowModule
import com.thindie.avezer.feature.home.places.PlacesScreen
import com.thindie.avezer.feature.home.places.ScreenCommand
import com.thindie.avezer.feature.home.places.ScreenState
import com.thindie.avezer.feature.home.places.exec
import com.thindie.avezer.feature.home.places.places
import com.thindie.avezer.feature.home.places.placesScreenErrorMapper

class HomeFlow(
    private val router: Router,
     val flowModule: AppFlowModule,
) : ScreenFlow<Route, Unit>(router) {

    override fun start() {
        router.push(places)
    }
}
