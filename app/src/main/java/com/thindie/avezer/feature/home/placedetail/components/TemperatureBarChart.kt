package com.thindie.avezer.feature.home.placedetail.components

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thindie.avezer.feature.home.domain.DailyForecast
import com.thindie.avezer.uikit.AppTheme
import com.thindie.avezer.uikit.VSpacer

@Composable
internal fun TemperatureBarChart(
  dailyForecasts: List<DailyForecast>,
  modifier: Modifier = Modifier,
) {
  Column {
    VSpacer(24.dp)
    val minTemp = dailyForecasts.minOfOrNull { it.temperatureMin }?.toFloat() ?: 0f
    val maxTemp = dailyForecasts.maxOfOrNull { it.temperatureMax }?.toFloat() ?: 30f
    val tempRange = (maxTemp - minTemp).takeIf { it > 0 } ?: 1f
    val density = LocalDensity.current
    val accent = AppTheme.colors.accentPrimary
    val backgroundSecondary = AppTheme.colors.backgroundSecondary
    val contentSecondary = AppTheme.colors.contentSecondary
    val contentTertiary = AppTheme.colors.contentTertiary

    val labelTextSizePx = with(density) { 14.sp.toPx() }
    val valueTextSizePx = with(density) { 12.sp.toPx() }

    val barCount = dailyForecasts.size.toFloat().coerceAtMost(7f)

    var animationTriggered by remember(dailyForecasts) { mutableStateOf(false) }
    LaunchedEffect(dailyForecasts) {
      animationTriggered = true
    }

    // Создаем список анимированных стейтов (от 0f до 1f) для каждого бара
    val animProgressList = List(dailyForecasts.size) { index ->
      animateFloatAsState(
        targetValue = if (animationTriggered) 1f else 0f,
        animationSpec = tween(
          durationMillis = 400,
          delayMillis = 200 + (index * 100),
          easing = LinearOutSlowInEasing
        ),
        label = "barAnimation_$index"
      )
    }

    Canvas(
      modifier = modifier
        .fillMaxWidth()
        .height(260.dp),
    ) {
      val chartWidth = size.width - 60f
      val chartHeight = size.height - 60f
      val barWidth = (chartWidth / barCount) * 0.6f
      val barSpacing = (chartWidth / barCount) * 0.4f


      var linesHeight = 0f
      for (i in 0..4) {
        val y = 20f + (chartHeight / 4 * i)
        drawLine(
          brush = SolidColor(backgroundSecondary),
          start = Offset(60f, y),
          end = Offset(size.width, y),
          strokeWidth = with(density) { 1.dp.toPx() },
        )
        linesHeight = y
      }

      val dayLabelPaint = Paint().asFrameworkPaint().apply {
        color = contentTertiary.toArgb()
        textSize = valueTextSizePx
        textAlign = android.graphics.Paint.Align.CENTER
      }

      // Draw temperature bars
      dailyForecasts.forEachIndexed { index, forecast ->
        if (index > 7) return@forEachIndexed
        val x = 60f + (index * (barWidth + barSpacing)) + barSpacing / 2f
        val minTempY = 20f + ((maxTemp - forecast.temperatureMin.toFloat()) / tempRange * chartHeight)
        val maxTempY = 20f + ((maxTemp - forecast.temperatureMax.toFloat()) / tempRange * chartHeight)

        val topY = minTempY.coerceAtMost(maxTempY)
        val bottomY = minTempY.coerceAtLeast(maxTempY)
        val fullBarHeight = bottomY - topY


        val progress = animProgressList.getOrNull(index)?.value ?: 1f
        val animatedHeight = fullBarHeight * progress

        val animatedTopY = bottomY - animatedHeight

        // Draw bar
        drawRect(
          brush = SolidColor(accent.copy(alpha = 0.3f)),
          topLeft = Offset(x, animatedTopY),
          size = Size(barWidth, animatedHeight),
        )


        if (progress > 0.1f) {
          // Max temp marker
          drawLine(
            brush = SolidColor(accent),
            start = Offset(x - 4f, maxTempY),
            end = Offset(x + barWidth + 4f, maxTempY),
            strokeWidth = with(density) { 2.dp.toPx() },
          )

          // Min temp marker
          drawLine(
            brush = SolidColor(accent),
            start = Offset(x - 4f, minTempY),
            end = Offset(x + barWidth + 4f, minTempY),
            strokeWidth = with(density) { 2.dp.toPx() },
          )
        }


        val centerX = x + (barWidth / 2f)
        val dayLabelString = forecast.time.substring(5).replace("-", "/")

        drawContext.canvas.nativeCanvas.drawText(
          dayLabelString,
          centerX,
          linesHeight + 40f,
          dayLabelPaint,
        )
      }

      // Draw temperature labels on the left
      val labelPaint = Paint().asFrameworkPaint().apply {
        color = contentSecondary.toArgb()
        textSize = labelTextSizePx
      }

      for (i in 0..4) {
        val temp = maxTemp - (tempRange / 4 * i)
        val y = 20f + (chartHeight / 4 * i) - 6f
        drawContext.canvas.nativeCanvas.drawText(
          "${temp.toInt()}°",
          10f,
          y,
          labelPaint,
        )
      }
    }
    VSpacer(24.dp)
  }
}
