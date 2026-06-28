package com.thindie.avezer.uikit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

private const val CONTENT_ALPHA_DISABLED = 0.3f

@Composable
fun TextField(
  modifier: Modifier = Modifier,
  value: String,
  onValueChange: (String) -> Unit,
  placeholder: String = "",
  enabled: Boolean = true,
  singleLine: Boolean = false,
  maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
  textStyle: TextStyle = TextStyle.Default,
  leadingContent: (@Composable () -> Unit)? = null,
  trailingContent: (@Composable () -> Unit)? = null,
) {
  val containerColor =
    when {
      !enabled -> AppTheme.colors.backgroundSecondary.copy(alpha = CONTENT_ALPHA_DISABLED)
      else -> AppTheme.colors.backgroundSecondary
    }

  BasicTextField(
    modifier = modifier,
    value = value,
    onValueChange = onValueChange,
    enabled = enabled,
    singleLine = singleLine,
    maxLines = maxLines,
    textStyle = textStyle.copy(color = AppTheme.colors.contentPrimary),
    decorationBox = { innerText ->
      Row(
        modifier =
          Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(
              color = containerColor,
              shape = RoundedCornerShape(16.dp),
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        if (value.isEmpty() && placeholder.isNotEmpty()) {
          Text(
            text = placeholder,
            color = AppTheme.colors.contentSecondary,
          )
        } else {
          if (leadingContent != null) {
            leadingContent()
            HSpacer(8.dp)
          }
          innerText()
          if (trailingContent != null) {
            HSpacer(8.dp)
            trailingContent()
          }
        }
      }
    },
  )
}
