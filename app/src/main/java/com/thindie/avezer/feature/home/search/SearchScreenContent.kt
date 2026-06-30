package com.thindie.avezer.feature.home.search

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.thindie.avezer.R
import com.thindie.avezer.engine.ScreenScope
import com.thindie.avezer.feature.home.domain.WeatherSearchResult
import com.thindie.avezer.uikit.Action
import com.thindie.avezer.uikit.AppScreen
import com.thindie.avezer.uikit.AppTheme
import com.thindie.avezer.uikit.Button
import com.thindie.avezer.uikit.TextField
import com.thindie.avezer.uikit.VSpacer
import com.thindie.avezer.uikit.WSpacer

@Preview(name = "Search Preview")
@Composable
private fun SearchPreview() {
  val mockState =
    SearchScreenState(
      query = "Moscow",
      results =
        listOf(
          WeatherSearchResult(city = "Moscow", lat = 55.7558, lon = 37.6173, isFavorite = true),
          WeatherSearchResult(city = "Moskva", lat = 55.7558, lon = 37.6173, isFavorite = false),
        ),
      favorites = listOf("Moscow"),
    )

  SearchScreenContent(state = mockState)
}

@Composable
internal fun SearchScreen(scope: ScreenScope<SearchScreenState, SearchScreenCommand>) {
  val state by scope.state.collectAsState()
  AppScreen(
    screenScope = scope,
    primary =
      Action(
        listener = { scope.send(SearchScreenCommand.Back) },
        resRef = R.drawable.ic_arrow_back_24,
      ),
  ) {
    BackHandler { scope.send(SearchScreenCommand.Back) }

    SearchScreenContent(state = state)
  }
}

@Composable
private fun SearchScreenContent(state: SearchScreenState) {
  Column(
    modifier = Modifier.imePadding().fillMaxSize().padding(16.dp),
  ) {
    Text(
      text = stringResource(R.string.search_title),
      style = AppTheme.typography.headlineLarge,
      color = AppTheme.colors.contentPrimary,
    )

    VSpacer(24.dp)

    TextField(
      value = state.query,
      onValueChange = { /* preview only */ },
      placeholder = stringResource(R.string.places_search_button),
      modifier = Modifier.fillMaxWidth(),
      singleLine = true,
      leadingContent = {
        Image(
          painter = androidx.compose.ui.res.painterResource(id = R.drawable.ic_search_24),
          contentDescription = null,
          modifier = Modifier.size(20.dp),
        )
      },
      trailingContent = {
        if (state.query.isNotEmpty()) {
          Image(
            painter = androidx.compose.ui.res.painterResource(id = R.drawable.ic_close_16),
            contentDescription = null,
            modifier = Modifier.size(16.dp),
          )
        }
      },
    )

    VSpacer(8.dp)

    state.results.forEach { result ->
      SearchResultItem(
        result = result,
        onClick = {},
      )
    }
    WSpacer()
    AnimatedVisibility(
      modifier = Modifier.padding(horizontal = 16.dp),
      visible = state.query.length > 4,
    ) {
      Button(
        modifier = Modifier.padding(horizontal = 16.dp),
        text = stringResource(id = R.string.places),
        onClick = {},
      )
    }
  }
}

@Composable
private fun SearchResultItem(
  result: WeatherSearchResult,
  onClick: () -> Unit,
) {
  Card(
    modifier = Modifier.fillMaxWidth().clickable { onClick() },
    colors = CardDefaults.cardColors(containerColor = AppTheme.colors.backgroundSecondary),
    shape = RoundedCornerShape(12.dp),
  ) {
    Row(
      modifier = Modifier.padding(12.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = result.city,
          style = AppTheme.typography.titleMedium,
          color = AppTheme.colors.contentPrimary,
        )
        VSpacer(2.dp)
        Text(
          text = "${result.lat}, ${result.lon}",
          style = AppTheme.typography.bodySmall,
          color = AppTheme.colors.contentSecondary,
        )
      }
      Text(
        text = if (result.isFavorite) "★" else "☆",
        style = AppTheme.typography.titleMedium,
        color = AppTheme.colors.accentPrimary,
      )
    }
  }
}
