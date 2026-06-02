package com.thindie.avezer.feature.home.places

import androidx.compose.runtime.Immutable
import com.thindie.avezer.engine.ScreenScope
import com.thindie.avezer.engine.ViewState
import com.thindie.avezer.engine.stateSink
import com.thindie.avezer.engine.sub
import com.thindie.avezer.engine.transition
import com.thindie.avezer.feature.home.domain.MainRepository
import com.thindie.avezer.feature.home.domain.Weather

@Immutable
data class ScreenState(val forecast: List<Weather>? = null) : ViewState

internal fun ScreenScope<ScreenState, ScreenCommand>.subscriptions(repository: MainRepository) {
  stateSink {
    sub (repository.forecast).transition(
      block = { _, forecast -> ScreenState(forecast) }
    )
  }
}
