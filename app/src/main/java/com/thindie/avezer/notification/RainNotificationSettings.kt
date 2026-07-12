package com.thindie.avezer.notification

import android.content.Context
import androidx.core.content.edit

/**
 * Settings for rain notification configuration.
 */
object RainNotificationSettings {
  private const val PREFS_NAME = "rain_notification_prefs"
  private const val KEY_NOTIFICATION_RANGE_DAYS = "notification_range_days"
  private const val DEFAULT_RANGE_DAYS = 2

  fun getNotificationRangeDays(context: Context): Int {
    return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
      .getInt(KEY_NOTIFICATION_RANGE_DAYS, DEFAULT_RANGE_DAYS)
  }

  fun setNotificationRangeDays(
    context: Context,
    days: Int,
  ) {
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit {
      putInt(KEY_NOTIFICATION_RANGE_DAYS, days.coerceIn(1, 3))
    }
  }
}
