package com.thindie.avezer.feature.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thindie.avezer.R
import com.thindie.avezer.engine.Command
import com.thindie.avezer.engine.Route
import com.thindie.avezer.engine.RouteFactory
import com.thindie.avezer.engine.Router
import com.thindie.avezer.engine.ScreenFlow
import com.thindie.avezer.engine.ScreenScope
import com.thindie.avezer.engine.stateSink
import com.thindie.avezer.engine.sub
import com.thindie.avezer.engine.transition
import com.thindie.avezer.feature.home.data.di.AppFlowModule
import com.thindie.avezer.feature.home.domain.Weather
import com.thindie.avezer.uikit.Action
import com.thindie.avezer.uikit.AppScreen
import com.thindie.avezer.uikit.AppTheme
import com.thindie.avezer.uikit.LocalThemeSwitcher
import com.thindie.avezer.uikit.ThemeSwitcher
import com.thindie.avezer.uikit.VSpacer


class HomeFlow(
  private val router: Router,
  private val flowModule: AppFlowModule,
) : ScreenFlow<Route, Unit>(router) {


  override fun start() {
    router.push(select())
  }

  private fun stateSink(screenScope: ScreenScope<ViewState, ScreenCommand>) {
    screenScope.stateSink {
      sub(flowModule.repository.forecast).transition(
        ) { s, forecast: List<Weather>? ->
          s.copy(forecast = forecast)
        }
    }
  }


  fun select() = RouteFactory.create(
      initialState = ViewState(),
      stateSink = ::stateSink,
    initialCommand = RouteFactory.InitialCommand { ScreenCommand.Fetch },
      execute = { c: ScreenCommand, s: ViewState ->
        when (c) {
          ScreenCommand.Fetch -> {
            flowModule.repository.read("Kaliningrad")
            s
          }

          ScreenCommand.Back -> {
            s
          }
        }
      },
      routeContent = { HomeScreen() }
    )
}

private data class ViewState(val forecast: List<Weather>? = null) : com.thindie.avezer.engine.State
private sealed interface ScreenCommand : Command {
  data object Fetch : ScreenCommand
  data object Back : ScreenCommand
}

@Composable
private fun ScreenScope<ViewState, ScreenCommand>.HomeScreen() {
  val themeSwitcher = LocalThemeSwitcher.current
  val themeColors = LocalThemeSwitcher.current.themeFlow.collectAsState(null)
  val isDark = when (themeColors.value) {
    null -> isSystemInDarkTheme()
    ThemeSwitcher.Choice.Dark -> true
    ThemeSwitcher.Choice.Light -> false
    ThemeSwitcher.Choice.Auto -> isSystemInDarkTheme()
  }
  AppScreen(
    secondary = Action(
    resRef = R.drawable.ic_theme_24, listener = {
      themeSwitcher.set(
        if (isDark) ThemeSwitcher.Choice.Light else ThemeSwitcher.Choice.Dark
      )
    }
    )
  ) {
    BackHandler { send(ScreenCommand.Back) }
    val st by state.collectAsState()
    st.forecast?.forEach {
      weather ->
      WeatherCard(weather)
    }
  }
}


@Composable
fun WeatherCard(
  weather: Weather,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier
      .padding(24.dp)
      .fillMaxWidth()
  ) {
    Text(
      text = weather.city,
      style = AppTheme.typography.headlineMedium,
      color = AppTheme.colors.contentPrimary
    )

    VSpacer(16.dp)

    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text(
        text = "${weather.temperature.toInt()}°C",
        fontSize = 64.sp,
        color = AppTheme.colors.contentSecondary
      )

      Text(
        text = weather.emoji, fontSize = 72.sp
      )
    }

    VSpacer(12.dp)

    Text(
      text = stringResource(weather.weatherCodeRef),
      style = AppTheme.typography.titleMedium,
      color = AppTheme.colors.contentPrimary
    )

    if (weather.windSpeed != null || weather.humidity != null) {
      VSpacer(24.dp)
      Divider(color = AppTheme.colors.backgroundSecondary)
      VSpacer(16.dp)

      Row(
        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
      ) {
        weather.humidity?.let {
          WeatherMetricItem(label = "Влажность", value = "$it%")
        }
        weather.windSpeed?.let {
          WeatherMetricItem(label = "Ветер", value = "${it.toInt()} км/ч")
        }
      }
    }
  }
}


@Composable
private fun WeatherMetricItem(
  label: String,
  value: String,
) {
  Column {
    Text(
      text = label, style = AppTheme.typography.bodyMedium, color = AppTheme.colors.contentPrimary
    )
    Text(
      text = value,
      style = AppTheme.typography.titleMedium,
      color = AppTheme.colors.contentSecondary
    )
  }
}
