package com.thindie.avezer.widget.updater

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.thindie.avezer.widget.WeatherGlanceAppWidget
import com.thindie.avezer.widget.data.WeatherDataProviderHolder
import java.util.concurrent.TimeUnit

/**
 * WorkManager Worker for periodic widget updates.
 */
class WidgetUpdateWorker(
  context: Context,
  workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {
  override suspend fun doWork(): Result {
    return try {
      val dataProvider = WeatherDataProviderHolder.getDataProvider() ?: return Result.failure()
      dataProvider.fetch()
      updateAllWidgets(applicationContext)

      Result.success()
    } catch (e: Exception) {
      Result.failure()
    }
  }

  private suspend fun updateAllWidgets(context: Context) {
    val manager = GlanceAppWidgetManager(context)
    val glanceIds = manager.getGlanceIds(WeatherGlanceAppWidget::class.java)

    for (glanceId in glanceIds) {
      try {
        WeatherGlanceAppWidget().update(context, glanceId)
      } catch (e: Exception) {
        // Ignore individual widget update failures
      }
    }
  }

  companion object {
    private const val WORK_NAME = "WeatherWidgetUpdate"
    private const val UPDATE_INTERVAL_HOURS = 2L

    fun startPeriodicUpdates(context: Context) {
      val constraints =
        Constraints.Builder()
          .setRequiredNetworkType(NetworkType.CONNECTED)
          .build()

      val workRequest =
        PeriodicWorkRequestBuilder<WidgetUpdateWorker>(
          UPDATE_INTERVAL_HOURS,
          TimeUnit.HOURS,
        )
          .setConstraints(constraints)
          .build()

      WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        WORK_NAME,
        ExistingPeriodicWorkPolicy.UPDATE,
        workRequest,
      )
    }

    fun stopUpdates(context: Context) {
      WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }
  }
}
