package com.thindie.avezer.application

import android.content.Context
import com.thindie.avezer.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private var weatherMap: Map<Int, Int>? = null
private var dayOfWeekStrings: Map<Int, String>? = null
private var timeJustNow: String? = null
private var timeToday: String? = null
private var timeYesterday: String? = null

private val weatherEmojis =
  mapOf(
    // Clear / Cloudy
    0 to "☀️",
    // Clear sky
    1 to "🌤️",
    // Mainly clear
    2 to "⛅",
    // Partly cloudy
    3 to "☁️",
    // Overcast
    // Fog
    45 to "🌫️",
    // Fog
    48 to "🌫️",
    // Depositing rime fog
    // Drizzle & Rain
    51 to "🌦️",
    // Drizzle Light
    53 to "🌦️",
    // Drizzle Moderate
    55 to "🌧️",
    // Drizzle Dense
    56 to "🧊",
    // Freezing Drizzle Light
    57 to "🧊",
    // Freezing Drizzle Dense
    61 to "💧",
    // Rain Slight
    63 to "🌧️",
    // Rain Moderate
    65 to "🌊",
    // Rain Heavy
    66 to "🥶",
    // Freezing Rain Light
    67 to "🥶",
    // Freezing Rain Heavy
    // Snow
    71 to "🌨️",
    // Snow Slight
    73 to "❄️",
    // Snow Moderate
    75 to "☃️",
    // Snow Heavy
    77 to "🍚",
    // Snow grains (крупа)
    // Showers
    80 to "🌦️",
    // Rain showers Slight
    81 to "🌧️",
    // Rain showers Moderate
    82 to "☔",
    // Rain showers Violent
    85 to "🌨️",
    // Snow showers Slight
    86 to "❄️",
    // Snow showers Heavy
    // Thunderstorm
    95 to "⚡",
    // Thunderstorm
    96 to "⛈️",
    // Thunderstorm + Hail Slight
    99 to "🌪️",
    // Thunderstorm + Hail Heavy
  )

object AppStrings {
  lateinit var errorUnexpected: String
    private set

  private val allCodes =
    listOf(
      0, 1, 2, 3, 45, 48, 51, 53, 55, 56, 57,
      61, 63, 65, 66, 67, 71, 73, 75, 77,
      80, 81, 82, 85, 86, 95, 96, 99,
    )

  private val dayOfWeekIds = listOf(1, 2, 3, 4, 5, 6, 7)

  suspend fun init(context: Context) {
    errorUnexpected = context.getString(R.string.error_unexpected)
    withContext(Dispatchers.IO) {
      if (weatherMap == null) {
        weatherMap =
          allCodes.associateWith { code ->
            context.resources.getIdentifier(
              "weather_code_$code",
              "string",
              context.packageName,
            ).let { if (it != 0) it else R.string.weather_code_unknown }
          }
      }
      if (dayOfWeekStrings == null) {
        dayOfWeekStrings =
          dayOfWeekIds.associateWith { id ->
            context.resources.getIdentifier(
              "day_of_week_$id",
              "string",
              context.packageName,
            ).let { resId ->
              if (resId != 0) {
                context.getString(resId)
              } else {
                context.getString(R.string.day_of_week_unknown)
              }
            }
          }
      }
      timeJustNow = context.getString(R.string.time_just_now)
      timeToday = context.getString(R.string.today)
      timeYesterday = context.getString(R.string.yesterday)
    }
  }
}

fun weatherCodeRef(code: Int): Int {
  return weatherMap?.get(code) ?: R.string.weather_code_unknown
}

fun dayOfWeekString(dayValue: Int): String {
  return dayOfWeekStrings?.get(dayValue) ?: "---"
}

fun weatherEmojiString(code: Int): String = weatherEmojis[code] ?: "❓"

fun timeJustNow(
  minutes: Int?,
  context: Context,
): String =
  if (minutes == null) {
    timeJustNow
  } else {
    context.getString(R.string.time_minutes_ago, minutes)
  }.orEmpty()

fun timeToday(): String = timeToday ?: "Today"

fun timeYesterday(): String = timeYesterday ?: "Yesterday"
