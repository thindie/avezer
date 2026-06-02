package com.thindie.avezer.feature.home.places

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.thindie.avezer.R
import com.thindie.avezer.engine.ScreenScope
import com.thindie.avezer.uikit.Action
import com.thindie.avezer.uikit.AppScreen
import com.thindie.avezer.uikit.LocalThemeSwitcher
import com.thindie.avezer.uikit.ThemeSwitcher

@Composable
internal fun ScreenScope<ScreenState, ScreenCommand>.PlacesScreen() {
  val themeSwitcher = LocalThemeSwitcher.current
  val isDark by remember { mutableStateOf(false) }

  AppScreen(
    secondary =
      Action(
        resRef = R.drawable.ic_theme_24,
        listener = {
          themeSwitcher.set(
            if (isDark) ThemeSwitcher.Choice.Light else ThemeSwitcher.Choice.Dark,
          )
        },
      ),
  ) {
    PlacesContent()
  }
}

@Composable
internal fun PlacesContent() {
  BackHandler { }
}
