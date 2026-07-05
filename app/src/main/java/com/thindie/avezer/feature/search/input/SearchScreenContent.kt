package com.thindie.avezer.feature.search.input

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.thindie.avezer.R
import com.thindie.avezer.engine.ScreenScope
import com.thindie.avezer.feature.home.domain.FavoriteLocation
import com.thindie.avezer.feature.home.domain.WeatherSearchResult
import com.thindie.avezer.uikit.AppScreen
import com.thindie.avezer.uikit.AppTheme
import com.thindie.avezer.uikit.Button
import com.thindie.avezer.uikit.TextField
import com.thindie.avezer.uikit.Toggle
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
          WeatherSearchResult(city = "Moscow", lat = 55.7558, lon = 37.6173, isFavourite = true),
          WeatherSearchResult(city = "Moskva", lat = 55.7558, lon = 37.6173, isFavourite = false),
        ),
    )

  SearchScreenContent(
    state = mockState,
    onSearchQuery = { },
    onSearchResultClick = { },
    onToggleFavorite = { },
    onConfirmSearch = { },
  )
}

@Composable
internal fun SearchScreen(scope: ScreenScope<SearchScreenState, SearchScreenCommand>) {
  val state by scope.state.collectAsState()
  AppScreen(
    screenScope = scope,
  ) {
    BackHandler { scope.send(SearchScreenCommand.Back) }

    SearchScreenContent(
      state = state,
      onSearchQuery = { scope.send(SearchScreenCommand.Search(it)) },
      onSearchResultClick = { scope.send(SearchScreenCommand.SelectCity(it)) },
      onToggleFavorite = { scope.send(SearchScreenCommand.ToggleFavorite(it)) },
      onConfirmSearch = { scope.send(SearchScreenCommand.ConfirmSearch) },
    )
  }
}

@Composable
private fun SearchScreenContent(
  state: SearchScreenState,
  onSearchQuery: (String) -> Unit,
  onSearchResultClick: (WeatherSearchResult) -> Unit,
  onToggleFavorite: (FavoriteLocation) -> Unit,
  onConfirmSearch: () -> Unit,
) {
  Column(
    modifier =
      Modifier.imePadding()
        .fillMaxSize()
        .padding(16.dp),
  ) {
    Column(
      modifier =
        Modifier
          .verticalScroll(rememberScrollState()),
    ) {
      Text(
        text = stringResource(R.string.search_title),
        style = AppTheme.typography.headlineLarge,
        color = AppTheme.colors.contentPrimary,
      )
      VSpacer(2.dp)
      Text(
        text = stringResource(R.string.search_hint),
        style = AppTheme.typography.labelMedium,
        color = AppTheme.colors.contentSecondary,
      )

      VSpacer(24.dp)

      TextField(
        value = state.query,
        onValueChange = { onSearchQuery(it) },
        placeholder = stringResource(R.string.places_search_button),
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        leadingContent = {
          Icon(
            painter = painterResource(id = R.drawable.ic_search_24),
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = AppTheme.colors.contentSecondary,
          )
        },
        trailingContent = {
          AnimatedVisibility(
            visible = state.query.isNotEmpty(),
          ) {
            Icon(
              modifier =
                Modifier
                  .background(AppTheme.colors.backgroundPrimary, CircleShape)
                  .padding(8.dp)
                  .size(16.dp)
                  .clickable(onClick = { onSearchQuery("") }),
              painter = painterResource(id = R.drawable.ic_close_16),
              contentDescription = null,
              tint = AppTheme.colors.contentPrimary,
            )
          }
        },
      )

      VSpacer(2.dp)
      Text(
        modifier = Modifier.padding(horizontal = 16.dp),
        text = stringResource(R.string.search_hint_multiple),
        style = AppTheme.typography.labelMedium,
        color = AppTheme.colors.contentSecondary,
      )

      VSpacer(1.dp)

      if (state.results.isNotEmpty()) {
        VSpacer(24.dp)
        Divider()
        VSpacer(24.dp)
      }

      state.results.forEach { result ->
        val favoriteLocation = FavoriteLocation(result.city, result.lat, result.lon)
        SearchResultItem(
          searchResult = result,
          isFavorite = result.isFavourite,
          onClick = { onSearchResultClick(result) },
          onToggleFavorite = { onToggleFavorite(favoriteLocation) },
        )
        VSpacer(8.dp)
      }
    }
    WSpacer()

    AnimatedVisibility(
      modifier = Modifier.padding(horizontal = 16.dp),
      visible = state.query.length > 4,
    ) {
      Button(
        modifier = Modifier.padding(horizontal = 16.dp),
        text = stringResource(id = R.string.search_title),
        onClick = { onConfirmSearch() },
      )
    }
  }
}

@Composable
private fun SearchResultItem(
  searchResult: WeatherSearchResult,
  isFavorite: Boolean,
  onClick: () -> Unit,
  onToggleFavorite: () -> Unit,
) {
  Card(
    modifier = Modifier.fillMaxWidth().clickable { onClick() },
    colors = CardDefaults.cardColors(containerColor = AppTheme.colors.backgroundPrimary),
    shape = RoundedCornerShape(12.dp),
    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = searchResult.city,
          style = AppTheme.typography.headlineSmall,
          color = AppTheme.colors.contentPrimary,
        )
        VSpacer(2.dp)
        val latStr = "%.2f".format(searchResult.lat).takeWhile { it != '.' }.padEnd(6, ' ')
        val lonStr = "%.2f".format(searchResult.lon).takeWhile { it != '.' }.padEnd(6, ' ')
        VSpacer(16.dp)
        Text(
          text = stringResource(R.string.latitude_label, latStr),
          style = AppTheme.typography.bodySmall,
          color = AppTheme.colors.contentSecondary.copy(alpha = 0.7f),
        )
        Text(
          text = stringResource(R.string.longitude_label, lonStr),
          style = AppTheme.typography.bodySmall,
          color = AppTheme.colors.contentSecondary.copy(alpha = 0.7f),
        )
      }
      Column {
        Toggle(checked = isFavorite, onClick = { onToggleFavorite() })
        VSpacer(2.dp)
        Text(
          text = stringResource(R.string.remember_button),
          style = AppTheme.typography.labelMedium,
          color = AppTheme.colors.contentSecondary.copy(alpha = 0.7f),
        )
      }
    }
  }
}
