package com.thindie.avezer.widget.data

fun interface WidgetInteraction {
  operator fun invoke(state: WidgetState)
}

enum class WidgetState {
  Error,
  Ok,
}
