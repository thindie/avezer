package com.thindie.avezer.feature.home.domain

import androidx.compose.runtime.Immutable

@Immutable
data class FavoriteLocation(
  val city: String,
  val lat: Double,
  val lon: Double,
)
