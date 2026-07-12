package com.thindie.avezer.widget.components

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.core.graphics.createBitmap
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import kotlin.collections.forEachIndexed

@Composable
fun GlanceDailyChartElement(
  data: List<Double>,
  labels: List<String>,
) {
  val gridColor = GlanceTheme.colors.backgroundSecondary.getColor(LocalContext.current).toArgb()
  val accentColor = GlanceTheme.colors.accentPrimary.getColor(LocalContext.current).toArgb()
  val textColor = GlanceTheme.colors.contentPrimary.getColor(LocalContext.current).toArgb()
  val dayColor = GlanceTheme.colors.contentTertiary.getColor(LocalContext.current).toArgb()
  val chartBitmap =
    generateTemperatureDailyChartBitmap(
      temperatures = data,
      timeLabels = labels,
      widthPx = 600,
      heightPx = 300,
      accentColor = accentColor,
      gridColor = gridColor,
      textColor = textColor,
      dayLabelsColor = dayColor,
    )

  Image(
    provider = ImageProvider(chartBitmap),
    contentDescription = "Weather Daily Chart",
    modifier =
      GlanceModifier
        .fillMaxWidth()
        .height(150.dp),
  )
}

fun generateTemperatureDailyChartBitmap(
  temperatures: List<Double>,
  timeLabels: List<String> = emptyList(),
  widthPx: Int = 600,
  heightPx: Int = 300,
  accentColor: Int = 0xFF2196F3.toInt(),
  gridColor: Int = 0xFFE0E0E0.toInt(),
  textColor: Int = 0xFF757575.toInt(),
  dayLabelsColor: Int = 0xFF757575.toInt(),
): Bitmap {
  return generateTemperatureChartBitmap(
    temperatures = temperatures,
    timeLabels = timeLabels,
    widthPx = widthPx,
    heightPx = heightPx,
    accentColor = accentColor,
    gridColor = gridColor,
    textColor = textColor,
    dayLabelsColor = dayLabelsColor,
  )
}

private fun generateTemperatureChartBitmap(
  temperatures: List<Double>,
  timeLabels: List<String> = emptyList(),
  widthPx: Int = 600,
  heightPx: Int = 300,
  accentColor: Int = 0xFF2196F3.toInt(),
  gridColor: Int = 0xFFE0E0E0.toInt(),
  dayLabelsColor: Int = 0xFFE0E0E0.toInt(),
  textColor: Int = 0xFF757575.toInt(),
): Bitmap {
  val bitmap = createBitmap(widthPx, heightPx)
  val canvas = Canvas(bitmap)

  val minTemp = temperatures.minOfOrNull { it }?.toFloat() ?: 0f
  val maxTemp = temperatures.maxOfOrNull { it }?.toFloat() ?: 30f
  val tempRange = (maxTemp - minTemp).takeIf { it > 0 } ?: 1f

  val leftPadding = 75f
  val rightPadding = 25f
  val topPadding = 40f
  val bottomPadding = 35f // Increased for time labels bar at the bottom

  val chartWidth = widthPx - leftPadding - rightPadding
  val chartHeight = heightPx - topPadding - bottomPadding

  val pointCount = temperatures.size.coerceIn(0, 24).toFloat()
  val pointSpacing = if (pointCount <= 1f) chartWidth else chartWidth / (pointCount - 1f)

  val gridPaint =
    Paint().apply {
      color = gridColor
      strokeWidth = 2f
      style = Paint.Style.STROKE
    }

  for (i in 0..4) {
    val y = topPadding + (chartHeight / 4 * i)
    canvas.drawLine(leftPadding, y, widthPx - rightPadding, y, gridPaint)
  }

  val linePaint =
    Paint().apply {
      color = accentColor
      strokeWidth = 6f
      style = Paint.Style.STROKE
      isAntiAlias = true
    }

  val pointPaint =
    Paint().apply {
      color = accentColor
      style = Paint.Style.FILL
      isAntiAlias = true
    }

  val valuePaint =
    Paint().apply {
      color = dayLabelsColor
      textSize = 20f
      textAlign = Paint.Align.CENTER
      isAntiAlias = true
    }

  // Draw lines, points, and values
  temperatures.forEachIndexed { index, temperature ->
    val x = leftPadding + (index * pointSpacing)
    val y = topPadding + ((maxTemp - temperature.toFloat()) / tempRange * chartHeight)

    // Lines between points
    if (index < temperatures.size - 1) {
      val nextX = leftPadding + ((index + 1) * pointSpacing)
      val nextY = topPadding + ((maxTemp - temperatures[index + 1].toFloat()) / tempRange * chartHeight)
      canvas.drawLine(x, y, nextX, nextY, linePaint)
    }

    canvas.drawCircle(x, y, 8f, pointPaint)
  }

  val labelPaint =
    Paint().apply {
      color = textColor
      textSize = 24f
      textAlign = Paint.Align.LEFT
      isAntiAlias = true
    }

  for (i in 0..4) {
    val temp = maxTemp - (tempRange / 4 * i)
    val y = topPadding + (chartHeight / 4 * i)

    val fontMetrics = labelPaint.fontMetrics
    val centerY = y - (fontMetrics.ascent + fontMetrics.descent) / 2f

    canvas.drawText("${temp.toInt()}°", 15f, centerY, labelPaint)
  }

  // Draw time labels at the bottom of the chart
  if (timeLabels.isNotEmpty()) {
    for ((index, label) in timeLabels.withIndex()) {
      val x = leftPadding + (index * pointSpacing)
      canvas.drawText(label, x, heightPx - bottomPadding / 4f, valuePaint)
    }
  }

  return bitmap
}
