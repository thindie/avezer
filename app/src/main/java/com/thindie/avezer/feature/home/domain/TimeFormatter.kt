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

  fun formatTime(isoTimeString: String): String = LocalDateTime.parse(isoTimeString).format(DateTimeFormatter.ofPattern("HH:mm"))
}
