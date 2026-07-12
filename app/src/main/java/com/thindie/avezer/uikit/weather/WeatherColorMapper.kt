package com.thindie.avezer.uikit.weather

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.thindie.avezer.application.weatherEmojiString
import com.thindie.avezer.feature.home.domain.DailyForecast
import com.thindie.avezer.uikit.AppTheme

object WeatherColorMapper {
  @Composable
  fun getAccentColor(dailyForecast: DailyForecast): Color =
    when (dailyForecast.weatherCodeRef) {
      // Clear / Cloudy
      in 0..3 -> AppTheme.colors.weatherSunny
      // Fog
      in 45..48 -> AppTheme.colors.weatherCloudy
      // Drizzle & Rain
      in 51..67 -> AppTheme.colors.weatherRainy
      // Snow
      in 71..86 -> AppTheme.colors.weatherSnowy
      // Thunderstorm
      in 95..99 -> AppTheme.colors.weatherThunderstorm
      else -> AppTheme.colors.contentTertiary
    }

  @Composable
  fun getAccentColor(emoji: String): Color =
    when (emoji) {
      "☀️", "🌤️" -> AppTheme.colors.weatherSunny
      "⛅", "☁️" -> AppTheme.colors.weatherCloudy
      "🌫️" -> AppTheme.colors.weatherCloudy
      "🌦️", "💧", "🌧️", "🌊" -> AppTheme.colors.weatherRainy
      "🧊", "🥶" -> AppTheme.colors.weatherSnowy
      "🌨️", "❄️", "☃️", "🍚" -> AppTheme.colors.weatherSnowy
      "⚡", "⛈️", "🌪️" -> AppTheme.colors.weatherThunderstorm
      else -> AppTheme.colors.contentTertiary
    }

  @Composable
  fun getAccentColor(weatherCode: Int): Color =
    when (weatherCode) {
      // Clear / Cloudy
      in 0..3 -> AppTheme.colors.weatherSunny
      // Fog
      in 45..48 -> AppTheme.colors.weatherCloudy
      // Drizzle & Rain
      in 51..67 -> AppTheme.colors.weatherRainy
      // Snow
      in 71..86 -> AppTheme.colors.weatherSnowy
      // Thunderstorm
      in 95..99 -> AppTheme.colors.weatherThunderstorm
      else -> AppTheme.colors.contentTertiary
    }

  @Composable
  fun getWeatherEmoji(dailyForecast: DailyForecast): String = weatherEmojiString(dailyForecast.weatherCodeRef)
}
