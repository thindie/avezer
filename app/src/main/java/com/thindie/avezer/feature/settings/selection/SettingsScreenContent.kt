package com.thindie.avezer.feature.settings.selection

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.thindie.avezer.R
import com.thindie.avezer.engine.ScreenScope
import com.thindie.avezer.uikit.Action
import com.thindie.avezer.uikit.AppScreen
import com.thindie.avezer.uikit.AppTheme
import com.thindie.avezer.uikit.LocalThemeSwitcher
import com.thindie.avezer.uikit.ThemeSwitcher
import com.thindie.avezer.uikit.VSpacer

@Composable
internal fun SettingsScreen(screenScope: ScreenScope<SettingsState, SettingsCommand>) {
  val themeSwitcher = LocalThemeSwitcher.current
  val themeChoice by themeSwitcher.themeFlow.collectAsState(ThemeSwitcher.Choice.Auto)
  val isDark =
    when (themeChoice) {
      ThemeSwitcher.Choice.Dark -> true
      ThemeSwitcher.Choice.Light -> false
      else -> isSystemInDarkTheme()
    }

  val screenState by screenScope.state.collectAsState()

  AppScreen(
    screenScope = screenScope,
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
    SettingsContent(
      currentThemeChoice = screenState.themeChoice,
      onBack = { screenScope.send(SettingsCommand.Back) },
      onSetThemeChoice = { themeChoice -> screenScope.send(SettingsCommand.SetThemeChoice(themeChoice)) },
    )
  }
}

@Composable
internal fun SettingsContent(
  currentThemeChoice: SettingsState.ThemeChoice?,
  onBack: () -> Unit,
  onSetThemeChoice: (SettingsState.ThemeChoice) -> Unit,
) {
  BackHandler { onBack() }

  Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Text(
      text = stringResource(R.string.settings_title),
      style = AppTheme.typography.headlineLarge,
    )

    Spacer(modifier = Modifier.padding(32.dp))

    ThemeChoiceCard(
      title = "Theme",
      currentChoice = currentThemeChoice,
      onSetChoice = onSetThemeChoice,
    )
  }
}

@Composable
internal fun ThemeChoiceCard(
  title: String,
  currentChoice: SettingsState.ThemeChoice?,
  onSetChoice: (SettingsState.ThemeChoice) -> Unit,
) {
  val choices =
    listOf(
      SettingsState.ThemeChoice.Auto,
      SettingsState.ThemeChoice.Light,
      SettingsState.ThemeChoice.Dark,
    )

  Column(modifier = Modifier.padding(16.dp)) {
    Text(
      text = title,
      style = AppTheme.typography.titleMedium,
    )

    Spacer(modifier = Modifier.padding(8.dp))

    choices.forEach { choice ->
      ThemeChoiceButton(
        choice = choice,
        isSelected = currentChoice == choice,
        onClick = { onSetChoice(choice) },
      )
      VSpacer(4.dp)
    }
  }
}

@Composable
internal fun ThemeChoiceButton(
  choice: SettingsState.ThemeChoice,
  isSelected: Boolean,
  onClick: () -> Unit,
) {
  val textContent =
    when (choice) {
      is SettingsState.ThemeChoice.Auto -> "Auto"
      is SettingsState.ThemeChoice.Light -> "Light"
      is SettingsState.ThemeChoice.Dark -> "Dark"
    }

  Box(
    modifier =
      Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp)
        .clickable(
          onClick = onClick,
        )
        .then(
          if (isSelected) {
            Modifier.background(AppTheme.colors.accentPrimary)
          } else {
            Modifier
          },
        ),
    contentAlignment = Alignment.CenterStart,
  ) {
    val color =
      if (isSelected) {
        AppTheme.colors.onAccentPrimary
      } else {
        AppTheme.colors.contentSecondary
      }
    Text(
      text = textContent,
      style = AppTheme.typography.bodyMedium,
      color = color,
    )
  }
}
