package com.thindie.avezer.feature.home.placehourly.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thindie.avezer.feature.home.domain.HourlyForecastItem
import com.thindie.avezer.feature.home.domain.TimeFormatter
import com.thindie.avezer.uikit.AppTheme

@Composable
internal fun TemperatureLineChart(
  hourlyForecasts: List<HourlyForecastItem>,
  modifier: Modifier = Modifier,
) {
  val minTemp = hourlyForecasts.minOfOrNull { it.temperature }?.toFloat() ?: 0f
  val maxTemp = hourlyForecasts.maxOfOrNull { it.temperature }?.toFloat() ?: 30f
  val tempRange = (maxTemp - minTemp).takeIf { it > 0 } ?: 1f

  val density = LocalDensity.current
  val strokeThinPx = with(density) { 1.dp.toPx() }
  val strokeThickPx = with(density) { 3.dp.toPx() }

  val labelTextSizePx = with(density) { 12.sp.toPx() }
  val valueTextSizePx = with(density) { 10.sp.toPx() }

  val accent = AppTheme.colors.accentPrimary
  val backgroundSecondary = AppTheme.colors.backgroundSecondary
  val contentSecondary = AppTheme.colors.contentSecondary
  val contentTertiary = AppTheme.colors.contentTertiary

  Canvas(
    modifier =
      modifier
        .fillMaxWidth()
        .height(150.dp),
  ) {
    val leftPadding = 70f
    val rightPadding = 20f
    val topPadding = 30f
    val bottomPadding = 40f

    val chartWidth = size.width - leftPadding - rightPadding
    val chartHeight = size.height - topPadding - bottomPadding

    val pointCount = hourlyForecasts.size.coerceIn(0, 24).toFloat()
    val pointSpacing = if (pointCount <= 1f) chartWidth else chartWidth / (pointCount - 1f)

    // Draw background grid lines
    for (i in 0..4) {
      val y = topPadding + (chartHeight / 4 * i)
      drawLine(
        brush = SolidColor(backgroundSecondary),
        start = Offset(leftPadding, y),
        end = Offset(size.width - rightPadding, y),
        strokeWidth = strokeThinPx,
      )
    }

    val valuePaint =
      Paint().asFrameworkPaint().apply {
        color = contentTertiary.toArgb()
        textSize = valueTextSizePx
        textAlign = android.graphics.Paint.Align.CENTER
      }
    val maxOf = hourlyForecasts.maxOf { it.temperature }
    val minOf = hourlyForecasts.minOf { it.temperature }

    var minHourDrawn = false
    var maxHourDrawn = false

    // Draw points and line
    hourlyForecasts.forEachIndexed { index, forecast ->
      val x = leftPadding + (index * pointSpacing)
      val y = topPadding + ((maxTemp - forecast.temperature.toFloat()) / tempRange * chartHeight)

      if (index < hourlyForecasts.size - 1) {
        val nextX = leftPadding + ((index + 1) * pointSpacing)
        val nextY = topPadding + ((maxTemp - hourlyForecasts[index + 1].temperature.toFloat()) / tempRange * chartHeight)

        drawLine(
          brush = SolidColor(accent),
          start = Offset(x, y),
          end = Offset(nextX, nextY),
          strokeWidth = strokeThickPx,
        )
      }

      drawCircle(
        color = accent,
        radius = 4f,
        center = Offset(x, y),
      )
      if (maxOf == forecast.temperature && !maxHourDrawn) {
        maxHourDrawn = true
        drawContext.canvas.nativeCanvas.drawText(
          TimeFormatter.formatHourlyTime(forecast.time),
          x,
          y - 10f,
          valuePaint,
        )
      }
      if (minOf == forecast.temperature && !minHourDrawn) {
        minHourDrawn = true
        drawContext.canvas.nativeCanvas.drawText(
          TimeFormatter.formatHourlyTime(forecast.time),
          x,
          y + 30f,
          valuePaint,
        )
      }
    }

    // Draw temperature labels on the left side
    val labelPaint =
      Paint().asFrameworkPaint().apply {
        color = contentSecondary.toArgb()
        textSize = labelTextSizePx
        textAlign = android.graphics.Paint.Align.LEFT
      }

    for (i in 0..4) {
      val temp = maxTemp - (tempRange / 4 * i)
      val y = topPadding + (chartHeight / 4 * i)

      val fontMetrics = labelPaint.fontMetrics
      val centerY = y - (fontMetrics.ascent + fontMetrics.descent) / 2f

      drawContext.canvas.nativeCanvas.drawText(
        "${temp.toInt()}°",
        15f,
        centerY,
        labelPaint,
      )
    }
  }
}
