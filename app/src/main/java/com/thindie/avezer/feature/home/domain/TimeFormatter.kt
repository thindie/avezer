package com.thindie.avezer.feature.home.domain

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

internal object TimeFormatter {
  private val dayNameToValue =
    mapOf(
      "monday" to 1,
      "tuesday" to 2,
      "wednesday" to 3,
      "thursday" to 4,
      "friday" to 5,
      "saturday" to 6,
      "sunday" to 7,
    )

  fun formatDailyDate(isoDateString: String): String {
    val date = LocalDate.parse(isoDateString)
    val dayValue = resolveDayOfWeekValue(date.dayOfWeek)
    val dayOfWeek = com.thindie.avezer.application.dayOfWeekString(dayValue)
    val formattedDate = date.format(DateTimeFormatter.ofPattern("d MMM", Locale.getDefault()))
    return "$dayOfWeek, $formattedDate"
  }

  private fun resolveDayOfWeekValue(dayOfWeek: java.time.DayOfWeek): Int {
    val direct = dayOfWeek.value
    if (direct in 1..7) return direct

    val name = dayOfWeek.toString().lowercase()
    dayNameToValue[name]?.let { return it }

    return 1 // default to Monday if everything fails
  }

  fun formatHourlyTime(isoTimeString: String): String {
    val dateTime = LocalDateTime.parse(isoTimeString)
    return dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
  }

  fun formatTime(isoTimeString: String): String =
    LocalDateTime.parse(isoTimeString).format(DateTimeFormatter.ofPattern("HH:mm"))

  /**
   * Formats a timestamp as a relative time string (e.g., "5 minutes ago", "Today at 14:30").
   */
  fun formatRelativeTime(timestampMillis: Long): String {
    val now = LocalDateTime.now()
    val then =
      LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(timestampMillis), java.time.ZoneId.systemDefault())

    val minutesDiff = java.time.Duration.between(then, now).toMinutes()

    return when {
      minutesDiff < 1 -> com.thindie.avezer.application.getString(R.string.time_just_now)
      minutesDiff < 60 -> {
        val count = minutesDiff.toInt()
        com.thindie.avezer.application.getString(R.string.time_minutes_ago, count)
      }

      then.toLocalDate() == now.toLocalDate() ->
        "${com.thindie.avezer.application.getString(R.string.today)} ${then.format(DateTimeFormatter.ofPattern("HH:mm"))}"

      then.toLocalDate() == now.minusDays(1).toLocalDate() ->
        "${com.thindie.avezer.application.getString(R.string.yesterday)} ${then.format(DateTimeFormatter.ofPattern("HH:mm"))}"

      else -> {
        val dayValue = resolveDayOfWeekValue(then.dayOfWeek)
        val dayName = com.thindie.avezer.application.dayOfWeekString(dayValue)
        "$dayName, ${then.format(DateTimeFormatter.ofPattern("d MMM", Locale.getDefault()))} at ${
          then.format(
            DateTimeFormatter.ofPattern("HH:mm")
          )
        }"
      }
    }
  }
}
