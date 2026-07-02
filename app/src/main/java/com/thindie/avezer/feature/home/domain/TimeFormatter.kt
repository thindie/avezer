package com.thindie.avezer.feature.home.domain

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

internal object TimeFormatter {
  private val dayOfWeekAbbreviations =
    mapOf(
      "Mon" to "Пн",
      "Tue" to "Вт",
      "Wed" to "Ср",
      "Thu" to "Чт",
      "Fri" to "Пт",
      "Sat" to "Сб",
      "Sun" to "Вс",
    )

  fun formatDailyDate(isoDateString: String): String {
    val date = LocalDate.parse(isoDateString)
    val dayOfWeek =
      date.dayOfWeek.toString()
        .removePrefix("Day")
        .let { abbr -> dayOfWeekAbbreviations[abbr] ?: abbr }
    val formattedDate = date.format(DateTimeFormatter.ofPattern("d MMM", Locale.getDefault()))
    return "$dayOfWeek, $formattedDate"
  }

  fun formatHourlyTime(isoTimeString: String): String {
    val dateTime = LocalDateTime.parse(isoTimeString)
    return dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
  }
}
