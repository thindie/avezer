package com.thindie.avezer.feature.settings.selection

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.thindie.avezer.engine.ServiceCommand
import com.thindie.avezer.feature.settings.domain.SettingsRepository
import com.thindie.avezer.uikit.Action
import com.thindie.avezer.uikit.AppScreen
import com.thindie.avezer.uikit.AppTheme
import com.thindie.avezer.uikit.HSpacer
import com.thindie.avezer.uikit.LocalThemeSwitcher
import com.thindie.avezer.uikit.ThemeSwitcher
import com.thindie.avezer.uikit.Toggle
import com.thindie.avezer.uikit.VSpacer

@Preview(name = "Settings Preview")
@Composable
private fun SettingsPreview() {
  val mockState =
    SettingsState(
      themeChoice = SettingsRepository.ThemeChoice.Auto,
      language = "en",
      startWithFavorites = false,
      legacyRestart = false,
    )

  SettingsScreenBody(state = mockState)
}

@Composable
internal fun SettingsScreenContent(scope: ScreenScope<SettingsState, SettingsCommand>) {
  AppScreen(
    screenScope = scope,
  ) {
    val state by scope.state.collectAsState()

    SettingsScreenBody(
      state = state,
      onBack = { scope.send(SettingsCommand.Back) },
      onSetThemeChoice = { scope.send(SettingsCommand.SetThemeChoice(it)) },
      onStartWithFavorites = { scope.send(SettingsCommand.StartWithFavorites) },
      onSelectLanguage = { scope.send(SettingsCommand.SelectLanguage(it)) },
      onManageLocations = { scope.send(SettingsCommand.ManageLocations) },
    )
  }
}

@Composable
private fun SettingsScreenBody(
  state: SettingsState,
  onBack: () -> Unit = {},
  onSetThemeChoice: (SettingsRepository.ThemeChoice) -> Unit = {},
  onStartWithFavorites: () -> Unit = {},
  onSelectLanguage: (String) -> Unit = {},
  onManageLocations: () -> Unit = {},
) {
  val themeSwitcher = LocalThemeSwitcher.current
  val theme by themeSwitcher.themeFlow.collectAsState(ThemeSwitcher.Choice.Auto)
  BackHandler { onBack() }
  if (state.legacyRestart) {
    val context = LocalActivity.current
    LaunchedEffect(Unit) {
      context?.recreate()
    }
  }
  Column(
    modifier =
      Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(16.dp),
  ) {
    Text(
      text = stringResource(R.string.settings_title),
      style = AppTheme.typography.headlineLarge,
      color = AppTheme.colors.contentPrimary,
    )

    // === Appearance ===
    VSpacer(24.dp)
    SectionTitle(stringResource(R.string.settings_section_appearance))
    VSpacer(16.dp)

    ThemeOption(
      label = stringResource(R.string.theme_auto),
      subtitle = stringResource(R.string.settings_theme_auto_subtitle),
      checked = theme == ThemeSwitcher.Choice.Auto,
      onCheckedChange = {
        val next =
          if (theme == ThemeSwitcher.Choice.Auto) ThemeSwitcher.Choice.Dark else ThemeSwitcher.Choice.Auto
        themeSwitcher.set(next)
        onSetThemeChoice(next.toSettingsThemeChoice())
      },
    )

    if (theme == ThemeSwitcher.Choice.Auto) {
      VSpacer(8.dp)
      Text(
        text = stringResource(R.string.theme_auto),
        style = AppTheme.typography.bodySmall,
        color = AppTheme.colors.contentSecondary,
      )
    }

    ThemeOption(
      label = stringResource(R.string.theme_light),
      checked = theme == ThemeSwitcher.Choice.Light,
      enabled = theme != ThemeSwitcher.Choice.Auto,
      onCheckedChange = {
        if (theme == ThemeSwitcher.Choice.Auto) return@ThemeOption
        themeSwitcher.set(ThemeSwitcher.Choice.Light)
        onSetThemeChoice(SettingsRepository.ThemeChoice.Light)
      },
    )

    ThemeOption(
      label = stringResource(R.string.theme_dark),
      checked = theme == ThemeSwitcher.Choice.Dark,
      enabled = theme != ThemeSwitcher.Choice.Auto,
      onCheckedChange = {
        if (theme == ThemeSwitcher.Choice.Auto) return@ThemeOption
        themeSwitcher.set(ThemeSwitcher.Choice.Dark)
        onSetThemeChoice(SettingsRepository.ThemeChoice.Dark)
      },
    )

    // === General ===
    VSpacer(24.dp)
    Divider()
    VSpacer(16.dp)
    SectionTitle(stringResource(R.string.settings_section_general))
    VSpacer(16.dp)

    ToggleRow(
      label = stringResource(R.string.settings_start_with_favorites_label),
      subtitle = stringResource(R.string.settings_start_with_favorites_subtitle),
      checked = state.startWithFavorites,
      onCheckedChange = onStartWithFavorites,
    )

    // === Language ===
    VSpacer(24.dp)
    Divider()
    VSpacer(16.dp)
    SectionTitle(stringResource(R.string.settings_section_language))
    VSpacer(16.dp)

    LanguageSection(
      label = stringResource(R.string.settings_language_label),
      subtitle = languageLabel(state.language),
      onClick = { onSelectLanguage(state.language.orEmpty()) },
    )

    // === Saved Locations ===
    if (state.savedLocations.isNotEmpty()) {
      VSpacer(24.dp)
      Divider()
      VSpacer(16.dp)
      SectionTitle(stringResource(R.string.settings_section_saved_locations))
      VSpacer(16.dp)

      OutlinedButton(
        onClick = { onManageLocations() },
        modifier = Modifier.fillMaxWidth(),
      ) {
        Text(
          text = stringResource(R.string.settings_manage_favorites_title),
          style = AppTheme.typography.bodyMedium,
          color = AppTheme.colors.accentPrimary,
        )
      }
    }
  }
}

// === Helper composables ===

@Composable
private fun SectionTitle(text: String) {
  Text(
    text = text,
    style = AppTheme.typography.titleMedium,
    color = AppTheme.colors.contentSecondary,
  )
}

@Composable
private fun ThemeOption(
  label: String,
  checked: Boolean,
  enabled: Boolean = true,
  subtitle: String? = null,
  onCheckedChange: () -> Unit,
) {
  Row(
    modifier =
      Modifier
        .fillMaxWidth()
        .clickable(enabled = enabled, onClick = onCheckedChange)
        .padding(12.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = label,
        style = AppTheme.typography.titleMedium,
        color = AppTheme.colors.contentPrimary,
      )
      if (subtitle != null) {
        VSpacer(2.dp)
        Text(text = subtitle, style = AppTheme.typography.bodySmall, color = AppTheme.colors.contentSecondary)
      }
    }
    Toggle(checked = checked, enabled = enabled)
    if (!enabled) {
      HSpacer(2.dp)
      Image(
        painter = painterResource(R.drawable.ic_lock_24),
        contentDescription = null,
        modifier = Modifier.padding(start = 8.dp),
      )
    }
  }
}

@Composable
internal fun ToggleRow(
  label: String,
  subtitle: String,
  checked: Boolean,
  onCheckedChange: () -> Unit,
) {
  Row(
    modifier =
      Modifier
        .fillMaxWidth()
        .clickable(
          onClick = onCheckedChange,
          indication = null,
          interactionSource = null,
        )
        .padding(12.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Column(modifier = Modifier.weight(1f)) {
      Text(text = label, style = AppTheme.typography.titleMedium, color = AppTheme.colors.contentPrimary)
      VSpacer(2.dp)
      Text(text = subtitle, style = AppTheme.typography.bodySmall, color = AppTheme.colors.contentSecondary)
    }
    Toggle(checked = checked)
  }
}

@Composable
private fun LanguageSection(
  label: String,
  subtitle: String,
  onClick: () -> Unit,
) {
  Row(
    modifier =
      Modifier
        .fillMaxWidth()
        .clickable(onClick = onClick)
        .padding(12.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Column(modifier = Modifier.weight(1f)) {
      Text(text = label, style = AppTheme.typography.titleMedium, color = AppTheme.colors.contentPrimary)
      VSpacer(2.dp)
      Text(text = subtitle, style = AppTheme.typography.bodySmall, color = AppTheme.colors.contentSecondary)
    }
  }
}

private fun selectLanguage(
  scope: ScreenScope<SettingsState, SettingsCommand>,
  currentLanguage: String,
) {
  scope.sendEvent(
    ServiceCommand.UiEvent.Decision(
      content = {
        LanguagePickerDialog(
          currentLanguage = currentLanguage,
          onClick = { code ->
            scope.send(SettingsCommand.SelectLanguage(code))
          },
        )
      },
      primaryAction = Action(listener = { }, resRef = R.string.btn_close),
    ),
  )
}

@Composable
private fun LanguagePickerDialog(
  currentLanguage: String,
  onClick: (String) -> Unit,
) {
  Column {
    Text(
      text = stringResource(R.string.settings_language_title),
      style = AppTheme.typography.headlineMedium,
      color = AppTheme.colors.contentPrimary,
    )
    VSpacer(24.dp)

    LanguageOption("en", currentLanguage) {
      onClick("en")
    }
    VSpacer(24.dp)
    LanguageOption("ru", currentLanguage) {
      onClick("ru")
    }
  }
}

@Composable
private fun LanguageOption(
  languageCode: String,
  currentLanguage: String?,
  onClick: () -> Unit,
) {
  Row(
    modifier = Modifier.clickable { onClick.invoke() },
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    val isSelected = currentLanguage == languageCode
    Text(
      text =
        when (languageCode) {
          "en" -> stringResource(R.string.language_en)
          "ru" -> stringResource(R.string.language_ru)
          else -> error("Unsupported locale")
        },
      style = AppTheme.typography.bodyMedium,
      color = if (isSelected) AppTheme.colors.contentPrimary else AppTheme.colors.contentSecondary,
    )
    if (isSelected) {
      Text(
        text = "✓",
        style = AppTheme.typography.labelMedium,
        color = AppTheme.colors.accentPrimary,
      )
    }
  }
}

@Composable
private fun languageLabel(language: String?): String {
  return when (language) {
    "en" -> stringResource(R.string.language_en)
    "ru" -> stringResource(R.string.language_ru)
    else -> language ?: ""
  }
}

private fun ThemeSwitcher.Choice.toSettingsThemeChoice(): SettingsRepository.ThemeChoice =
  when (this) {
    ThemeSwitcher.Choice.Auto -> SettingsRepository.ThemeChoice.Auto
    ThemeSwitcher.Choice.Light -> SettingsRepository.ThemeChoice.Light
    ThemeSwitcher.Choice.Dark -> SettingsRepository.ThemeChoice.Dark
  }
