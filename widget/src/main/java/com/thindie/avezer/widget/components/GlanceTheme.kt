package com.thindie.avezer.widget.components

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceComposable
import androidx.glance.LocalContext
import androidx.glance.color.ColorProvider
import androidx.glance.text.FontFamily
import androidx.glance.text.FontWeight
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider

@Immutable
internal data class GlanceColors(
  val contentPrimary: ColorProvider,
  val contentSecondary: ColorProvider,
  val contentTertiary: ColorProvider,
  val backgroundPrimary: ColorProvider,
  val backgroundSecondary: ColorProvider,
  val cardPrimary: ColorProvider,
  val accentPrimary: ColorProvider,
  val onAccentPrimary: ColorProvider,
  val successPrimary: ColorProvider,
  val errorPrimary: ColorProvider,
  val weatherSunny: ColorProvider,
  val weatherCloudy: ColorProvider,
  val weatherRainy: ColorProvider,
  val weatherSnowy: ColorProvider,
  val weatherThunderstorm: ColorProvider,
)

@SuppressLint("RestrictedApi")
private val ColorScheme =
  GlanceColors(
    contentPrimary = ColorProvider(day = Color(0xFF1C1B1F), night = Color(0xFFEDEAF5)),
    contentSecondary = ColorProvider(day = Color(0xFF3F3F44), night = Color(0xFFD8D5E0)),
    contentTertiary = ColorProvider(day = Color(0xFF6E6E73), night = Color(0xFFB8B5C0)),
    backgroundPrimary = ColorProvider(day = Color(0xFFFDFCFA), night = Color(0xFF17161A)),
    backgroundSecondary = ColorProvider(day = Color(0xFFF4F2EF), night = Color(0xFF353240)),
    cardPrimary = ColorProvider(day = Color(0xFFFFFEFC), night = Color(0xFF2A2733)),
    accentPrimary = ColorProvider(Color(0xFF2A9D8F)),
    onAccentPrimary = ColorProvider(day = Color(0xFF143B36), night = Color(0xFFFDFCFA)),
    successPrimary = ColorProvider(day = Color(0xFF387A3E), night = Color(0xFF5CB864)),
    errorPrimary = ColorProvider(day = Color(0xFFB3261E), night = Color(0xFFF05252)),
    weatherSunny = ColorProvider(day = Color(0xFFFFA726), night = Color(0xFFFFD54F)),
    weatherCloudy = ColorProvider(day = Color(0xFF78909C), night = Color(0xFF90A4AE)),
    weatherRainy = ColorProvider(day = Color(0xFF42A5F5), night = Color(0xFF64B5F6)),
    weatherSnowy = ColorProvider(day = Color(0xFF4FC3F7), night = Color(0xFF81D4FA)),
    weatherThunderstorm = ColorProvider(day = Color(0xFFAB47BC), night = Color(0xFFCE93D8)),
  )

private val LocalGlanceColors = compositionLocalOf { ColorScheme }
private val LocalGlanceTypography = compositionLocalOf<GlanceTypography> { error("No typography provided") }

internal object GlanceTheme {
  val colors: GlanceColors
    @Composable
    @GlanceComposable
    @ReadOnlyComposable
    get() = LocalGlanceColors.current

  val typography: GlanceTypography
    @Composable
    @GlanceComposable
    @ReadOnlyComposable
    get() = LocalGlanceTypography.current
}

@Composable
@GlanceComposable
internal fun GlanceTheme(
  fontScale: Float? = null,
  content: @Composable () -> Unit,
) {
  val context = LocalContext.current
  val displayMetrics = context.resources.displayMetrics
  val fScale = fontScale ?: (displayMetrics.scaledDensity / displayMetrics.density)
  val typography = GlanceTypography(context, fScale)
  CompositionLocalProvider(
    LocalGlanceColors provides ColorScheme,
    LocalGlanceTypography provides typography,
  ) {
    content()
  }
}

internal class GlanceTypography(private val context: Context, private val fontScale: Float) {
  fun headlineLarge(
    color: ColorProvider,
    textAlign: TextAlign = TextAlign.Start,
  ): TextStyle {
    return TextStyle(
      color = color,
      fontSize = context.applyFontScale(32.sp, fontScale),
      fontWeight = FontWeight.Bold,
      textAlign = textAlign,
      fontFamily = roboto,
    )
  }

  fun headlineMedium(
    color: ColorProvider,
    textAlign: TextAlign = TextAlign.Start,
  ): TextStyle =
    TextStyle(
      color = color,
      fontSize = context.applyFontScale(28.sp, fontScale),
      fontWeight = FontWeight.Bold,
      textAlign = textAlign,
      fontFamily = roboto,
    )

  fun titleLarge(
    color: ColorProvider,
    textAlign: TextAlign = TextAlign.Start,
  ): TextStyle =
    TextStyle(
      color = color,
      fontSize = context.applyFontScale(24.sp, fontScale),
      fontWeight = FontWeight.Medium,
      textAlign = textAlign,
      fontFamily = roboto,
    )

  fun titleMedium(
    color: ColorProvider,
    textAlign: TextAlign = TextAlign.Start,
  ): TextStyle =
    TextStyle(
      color = color,
      fontSize = context.applyFontScale(22.sp, fontScale),
      fontWeight = FontWeight.Medium,
      textAlign = textAlign,
      fontFamily = roboto,
    )

  fun titleSmall(
    color: ColorProvider,
    textAlign: TextAlign = TextAlign.Start,
  ): TextStyle =
    TextStyle(
      color = color,
      fontSize = context.applyFontScale(20.sp, fontScale),
      fontWeight = FontWeight.Medium,
      textAlign = textAlign,
      fontFamily = roboto,
    )

  fun bodyMedium(
    color: ColorProvider,
    textAlign: TextAlign = TextAlign.Start,
  ): TextStyle =
    TextStyle(
      color = color,
      fontSize = context.applyFontScale(18.sp, fontScale),
      fontWeight = FontWeight.Normal,
      textAlign = textAlign,
      fontFamily = roboto,
    )

  fun bodyLarge(
    color: ColorProvider,
    textAlign: TextAlign = TextAlign.Start,
  ): TextStyle =
    TextStyle(
      color = color,
      fontSize = context.applyFontScale(16.sp, fontScale),
      fontWeight = FontWeight.Normal,
      textAlign = textAlign,
      fontFamily = roboto,
    )

  fun bodySmall(
    color: ColorProvider,
    textAlign: TextAlign = TextAlign.Start,
  ): TextStyle =
    TextStyle(
      color = color,
      fontSize = context.applyFontScale(14.sp, fontScale),
      fontWeight = FontWeight.Medium,
      textAlign = textAlign,
      fontFamily = roboto,
    )

  fun labelLarge(
    color: ColorProvider,
    textAlign: TextAlign = TextAlign.Start,
  ): TextStyle =
    TextStyle(
      color = color,
      fontSize = context.applyFontScale(14.sp, fontScale),
      fontWeight = FontWeight.Medium,
      textAlign = textAlign,
      fontFamily = roboto,
    )

  fun labelMedium(
    color: ColorProvider,
    textAlign: TextAlign = TextAlign.Start,
  ): TextStyle =
    TextStyle(
      color = color,
      fontSize = context.applyFontScale(12.sp, fontScale),
      fontWeight = FontWeight.Medium,
      textAlign = textAlign,
      fontFamily = roboto,
    )

  fun labelSmall(
    color: ColorProvider,
    textAlign: TextAlign = TextAlign.Start,
  ): TextStyle =
    TextStyle(
      color = color,
      fontSize = context.applyFontScale(11.sp, fontScale),
      fontWeight = FontWeight.Medium,
      textAlign = textAlign,
      fontFamily = roboto,
    )
}

private fun Context.applyFontScale(
  value: TextUnit,
  factor: Float,
): TextUnit {
  val deviceDensityScaleFactor = resources.displayMetrics.scaledDensity / resources.displayMetrics.density

  return if (deviceDensityScaleFactor != factor) {
    value / deviceDensityScaleFactor * factor
  } else {
    value
  }
}

private val roboto = FontFamily("roboto")
