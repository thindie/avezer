package com.thindie.avezer.feature.home.domain

import android.content.Context
import com.thindie.avezer.application.dayOfWeekString
import com.thindie.avezer.application.timeJustNow
import com.thindie.avezer.application.timeToday
import com.thindie.avezer.application.timeYesterday
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
    val dayOfWeek = dayOfWeekString(dayValue)
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

  fun formatHourlyTimeFull(isoTimeString: String): String {
    val dateTime = LocalDateTime.parse(isoTimeString)
    return dateTime.format(DateTimeFormatter.ofPattern("d MMM", Locale.getDefault()))
  }

  fun formatTime(isoTimeString: String): String =
    LocalDateTime
      .parse(isoTimeString)
      .format(DateTimeFormatter.ofPattern("HH:mm"))

  /**
   * Formats a timestamp as a relative time string (e.g., "5 minutes ago", "Today at 14:30").
   */
  fun formatRelativeTime(
    timestampMillis: Long,
    context: Context,
  ): String {
    val now = LocalDateTime.now()
    val then =
      LocalDateTime
        .ofInstant(
          java.time.Instant.ofEpochMilli(timestampMillis),
          java.time.ZoneId.systemDefault(),
        )

    val minutesDiff = java.time.Duration.between(then, now).toMinutes()

    return when {
      minutesDiff < 1 -> timeJustNow(null, context)
      minutesDiff < 60 -> {
        val count = minutesDiff.toInt()
        timeJustNow(count, context)
      }

      then.toLocalDate() == now.toLocalDate() ->
        "${timeToday()} ${then.format(DateTimeFormatter.ofPattern("HH:mm"))}"

      then.toLocalDate() == now.minusDays(1).toLocalDate() ->
        "${timeYesterday()} ${then.format(DateTimeFormatter.ofPattern("HH:mm"))}"

      else -> {
        val dayValue = resolveDayOfWeekValue(then.dayOfWeek)
        val dayName = dayOfWeekString(dayValue)
        "$dayName, ${then.format(DateTimeFormatter.ofPattern("d MMM", Locale.getDefault()))} at ${
          then.format(
            DateTimeFormatter.ofPattern("HH:mm"),
          )
        }"
      }
    }
  }
}
