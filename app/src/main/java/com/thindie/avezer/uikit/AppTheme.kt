package com.thindie.avezer.uikit

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

@Immutable
class AppColors(
  val contentPrimary: Color,
  val contentSecondary: Color,
  val contentTertiary: Color,
  val buttonContentPrimary: Color,
  val backgroundPrimary: Color,
  val backgroundSecondary: Color,
  val cardPrimary: Color,
  val accentPrimary: Color,
  val onAccentPrimary: Color,
  val successPrimary: Color,
  val errorPrimary: Color,
  val weatherSunny: Color,
  val weatherCloudy: Color,
  val weatherRainy: Color,
  val weatherSnowy: Color,
  val weatherThunderstorm: Color,
)

private val LightColorScheme =
  AppColors(
    contentPrimary = Color(0xFF1C1B1F),
    contentSecondary = Color(0xFF3F3F44),
    contentTertiary = Color(0xFF6E6E73),
    backgroundPrimary = Color(0xFFFDFCFA),
    backgroundSecondary = Color(0xFFF4F2EF),
    cardPrimary = Color(0xFFFFFEFC),
    accentPrimary = Color(0xFF2A9D8F),
    onAccentPrimary = Color(0xFF143B36),
    successPrimary = Color(0xFF387A3E),
    errorPrimary = Color(0xFFB3261E),
    buttonContentPrimary = Color(0xFFFDFCFA),
    weatherSunny = Color(0xFFFFA726),
    weatherCloudy = Color(0xFF78909C),
    weatherRainy = Color(0xFF42A5F5),
    weatherSnowy = Color(0xFF4FC3F7),
    weatherThunderstorm = Color(0xFFAB47BC),
  )

private val DarkColorScheme =
  AppColors(
    contentPrimary = Color(0xFFEDEAF5),
    contentSecondary = Color(0xFFD8D5E0),
    contentTertiary = Color(0xFFB8B5C0),
    backgroundPrimary = Color(0xFF17161A),
    backgroundSecondary = Color(0xFF353240),
    cardPrimary = Color(0xFF2A2733),
    accentPrimary = Color(0xFF2A9D8F),
    onAccentPrimary = Color(0xFFFDFCFA),
    successPrimary = Color(0xFF5CB864),
    errorPrimary = Color(0xFFF05252),
    buttonContentPrimary = Color(0xFF17161A),
    weatherSunny = Color(0xFFFFD54F),
    weatherCloudy = Color(0xFF90A4AE),
    weatherRainy = Color(0xFF64B5F6),
    weatherSnowy = Color(0xFF81D4FA),
    weatherThunderstorm = Color(0xFFCE93D8),
  )

class ThemeSwitcher {
  private val _themeFlow =
    MutableSharedFlow<Choice>(
      replay = 1,
      onBufferOverflow = BufferOverflow.DROP_OLDEST,
      extraBufferCapacity = 1,
    )
  val themeFlow = _themeFlow.asSharedFlow()

  enum class Choice {
    Dark,
    Light,
    Auto,
  }

  fun set(choice: Choice) {
    _themeFlow.tryEmit(choice)
  }
}

private val LocalAppColors = compositionLocalOf { LightColorScheme }
private val LocalAppTypo = staticCompositionLocalOf { AppTypography }
val LocalThemeSwitcher = staticCompositionLocalOf { ThemeSwitcher() }

object AppTheme {
  val colors: AppColors
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current

  val typography: AppTypography
    @Composable
    @ReadOnlyComposable
    get() = LocalAppTypo.current
}

@Composable
fun AppTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) {
  val targetColors = if (darkTheme) DarkColorScheme else LightColorScheme

  val colorSpec: AnimationSpec<Color> = tween(durationMillis = 400)

  @Composable
  fun animateColor(target: Color) = animateColorAsState(target, colorSpec, label = "color").value

  val animatedColors =
    AppColors(
      contentPrimary = animateColor(targetColors.contentPrimary),
      contentSecondary = animateColor(targetColors.contentSecondary),
      contentTertiary = animateColor(targetColors.contentTertiary),
      backgroundPrimary = animateColor(targetColors.backgroundPrimary),
      backgroundSecondary = animateColor(targetColors.backgroundSecondary),
      cardPrimary = animateColor(targetColors.cardPrimary),
      accentPrimary = animateColor(targetColors.accentPrimary),
      successPrimary = animateColor(targetColors.successPrimary),
      onAccentPrimary = animateColor(targetColors.onAccentPrimary),
      errorPrimary = animateColor(targetColors.errorPrimary),
      buttonContentPrimary = animateColor(targetColors.buttonContentPrimary),
      weatherSunny = animateColor(targetColors.weatherSunny),
      weatherCloudy = animateColor(targetColors.weatherCloudy),
      weatherRainy = animateColor(targetColors.weatherRainy),
      weatherSnowy = animateColor(targetColors.weatherSnowy),
      weatherThunderstorm = animateColor(targetColors.weatherThunderstorm),
    )

  CompositionLocalProvider(
    LocalAppColors provides animatedColors,
    LocalAppTypo provides AppTypography,
  ) {
    content()
  }
}

@Immutable
object AppTypography {
  val headlineLarge =
    TextStyle(
      fontSize = 40.sp,
      lineHeight = 36.sp,
      letterSpacing = 1.26.sp,
      fontWeight = FontWeight.ExtraBold,
    )

  val headlineMedium =
    TextStyle(
      fontSize = 32.sp,
      lineHeight = 28.sp,
      letterSpacing = 1.24.sp,
      fontWeight = FontWeight.Bold,
    )

  val headlineSmall =
    TextStyle(
      fontSize = 24.sp,
      lineHeight = 28.sp,
      fontWeight = FontWeight.W400,
    )

  val titleLarge =
    TextStyle(
      fontSize = 20.sp,
      lineHeight = 24.sp,
      fontWeight = FontWeight.W400,
    )

  val titleMedium =
    TextStyle(
      fontSize = 18.sp,
      lineHeight = 22.sp,
      letterSpacing = 1.2.sp,
      fontWeight = FontWeight.W500,
    )
  val titleSmall =
    TextStyle(
      fontSize = 16.sp,
      lineHeight = 20.sp,
      fontWeight = FontWeight.W700,
    )

  val button =
    TextStyle(
      fontSize = 16.sp,
      lineHeight = 20.sp,
      letterSpacing = 1.2.sp,
      fontWeight = FontWeight.W700,
    )

  val bodyMedium =
    TextStyle(
      fontSize = 14.sp,
      lineHeight = 18.sp,
      fontWeight = FontWeight.W500,
    )

  val bodySmall =
    TextStyle(
      fontSize = 14.sp,
      lineHeight = 18.sp,
      fontWeight = FontWeight.W400,
    )

  val labelLarge =
    TextStyle(
      fontSize = 12.sp,
      lineHeight = 16.sp,
      fontWeight = FontWeight.W400,
    )

  val labelMedium =
    TextStyle(
      fontSize = 10.sp,
      lineHeight = 14.sp,
      fontWeight = FontWeight.W400,
    )

  val placeholder =
    TextStyle(
      fontSize = 13.sp,
      lineHeight = 16.sp,
      fontWeight = FontWeight.W400,
    )
}
