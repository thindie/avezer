package com.thindie.avezer.widget.settings

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import androidx.activity.ComponentActivity

/**
 * Settings Activity opened when user clicks on the widget.
 * Allows configuration of which location to display on the widget.
 */
class WidgetSettingsActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Set minimal content view for widget settings
    val container = FrameLayout(this).apply { setBackgroundColor(getThemeColor(android.R.attr.colorBackground)) }
    setContentView(container)

    val appWidgetId =
      intent?.extras?.getInt(
        android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID,
        android.appwidget.AppWidgetManager.INVALID_APPWIDGET_ID,
      ) ?: run {
        finish()
        return
      }

    if (appWidgetId == android.appwidget.AppWidgetManager.INVALID_APPWIDGET_ID) {
      finish()
      return
    }

    // Save widget ID for later use
    val resultValue =
      Intent().putExtra(
        android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID,
        appWidgetId,
      )

    setResult(Activity.RESULT_OK, resultValue)
    finish()
  }

  private fun getThemeColor(attr: Int): Int {
    val styledAttrs = theme.obtainStyledAttributes(intArrayOf(attr))
    val color = styledAttrs.getColor(0, 0)
    styledAttrs.recycle()
    return color
  }
}
