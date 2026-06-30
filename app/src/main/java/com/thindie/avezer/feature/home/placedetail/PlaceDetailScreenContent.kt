package com.thindie.avezer.feature.home.placedetail

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.thindie.avezer.engine.ScreenScope
import com.thindie.avezer.feature.home.placedetail.components.DailyForecastCard
import com.thindie.avezer.uikit.AppScreen

@Composable
internal fun PlaceDetailScreen(screenScope: ScreenScope<PlaceDetailState, PlaceDetailCommand>) {
  val screenState by screenScope.state.collectAsState()

  AppScreen(
    screenScope = screenScope,
  ) {
    PlaceDetailContent(
      dailyForecast = screenState.dailyForecast,
      onBack = { screenScope.send(PlaceDetailCommand.Back) },
    )
  }
}

@Composable
internal fun PlaceDetailContent(
  dailyForecast: List<com.thindie.avezer.feature.home.domain.DailyForecast>?,
  onBack: () -> Unit,
) {
  BackHandler { onBack() }

  if (dailyForecast.isNullOrEmpty()) {
    Column(modifier = Modifier.fillMaxSize()) {}
  } else {
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
    ) {
      items(dailyForecast) { forecast ->
        DailyForecastCard(dailyForecast = forecast)
      }
    }
  }
}
