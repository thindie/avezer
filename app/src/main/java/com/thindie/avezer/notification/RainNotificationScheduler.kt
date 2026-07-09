package com.thindie.avezer.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.thindie.avezer.MainActivity
import com.thindie.avezer.R
import com.thindie.avezer.feature.home.domain.ForecastRepository
import com.thindie.avezer.feature.home.domain.Weather
import kotlinx.coroutines.flow.firstOrNull
import java.time.LocalDate

/**
 * Scheduler for rain notifications based on forecast data.
 */
object RainNotificationScheduler {
  private const val CHANNEL_ID = "rain_notifications"
  private const val NOTIFICATION_ID = 1001

  @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
  suspend fun scheduleRainNotifications(
    context: Context,
    repository: ForecastRepository,
  ) {
    createNotificationChannel(context)

    val rangeDays = RainNotificationSettings.getNotificationRangeDays(context)
    val forecasts = repository.forecast.firstOrNull() ?: return

    for (weather in forecasts) {
      val rainForecast = findRainWithinDays(weather, rangeDays)
      if (rainForecast != null) {
        showRainNotification(context, weather.city, rainForecast)
      }
    }
  }

  private fun createNotificationChannel(context: Context) {
    val channel =
      NotificationChannel(
        CHANNEL_ID,
        context.getString(R.string.rain_notification_channel_name),
        NotificationManager.IMPORTANCE_DEFAULT,
      ).apply {
        description = context.getString(R.string.rain_notification_channel_description)
      }

    val manager = NotificationManagerCompat.from(context)
    manager.createNotificationChannel(channel)
  }

  private fun findRainWithinDays(
    weather: Weather,
    days: Int,
  ): String? {
    val now = LocalDate.now()
    val endDate = now.plusDays(days.toLong())

    for (dayForecast in weather.forecast) {
      val forecastDate = LocalDate.parse(dayForecast.time)
      if (forecastDate.isAfter(endDate)) break

      if (dayForecast.precipitationSum > 0.0) {
        return dayForecast.time
      }
    }

    return null
  }

  @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
  private fun showRainNotification(
    context: Context,
    city: String,
    rainDate: String,
  ) {
    val intent =
      Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
      }

    val pendingIntent =
      PendingIntent.getActivity(
        context,
        0,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
      )

    val notification =
      NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_arrow_back_24) // Use appropriate icon
        .setContentTitle(context.getString(R.string.rain_notification_title))
        .setContentText("${context.getString(R.string.rain_notification_message)} $city")
        .setStyle(
          NotificationCompat.BigTextStyle()
            .bigText("$city - ${context.getString(R.string.rain_notification_detail)}"),
        )
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .build()

    NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
  }

  fun cancelNotifications(context: Context) {
    NotificationManagerCompat.from(context).cancelAll()
  }
}
