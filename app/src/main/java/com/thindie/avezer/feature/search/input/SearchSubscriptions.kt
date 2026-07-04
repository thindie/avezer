package com.thindie.avezer.feature.search.input

import com.thindie.avezer.engine.ScreenScope
import com.thindie.avezer.engine.stateSink
import com.thindie.avezer.engine.sub
import com.thindie.avezer.engine.transition
import com.thindie.avezer.feature.home.domain.PlacesRepository

internal fun ScreenScope<SearchScreenState, SearchScreenCommand>.subscriptions(repository: PlacesRepository) {
  stateSink(this) { scope ->
    scope.sub(
      repository.result,
    ).transition(
      block = { _, results -> scope.state.value.copy(results = results) },
    )
  }
}
