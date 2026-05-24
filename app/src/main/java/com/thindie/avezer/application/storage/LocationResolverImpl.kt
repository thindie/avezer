package com.thindie.avezer.application.storage

import android.content.Context
import android.location.Geocoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.util.Locale

class LocationResolverImpl(private val context: Context) : LocationResolver {
  private val geocoder by lazy { Geocoder(context, Locale.getDefault()) }

  override suspend fun read(name: String): Pair<Double, Double>? {
    return withContext(Dispatchers.IO) {
      if (!isActive) return@withContext null
      val addresses = geocoder.getFromLocationName(name, 1)
      if (!isActive) return@withContext null
      addresses?.firstOrNull()?.let {
        it.latitude to it.longitude
      }
    }
  }

  override suspend fun read(
    lat: Double,
    lon: Double,
  ): Address? {
    return withContext(Dispatchers.IO) {
      if (!isActive) return@withContext null
      val addresses = geocoder.getFromLocation(lat, lon, 1)
      if (!isActive) return@withContext null
      val address = addresses?.firstOrNull()
      return@withContext Address(
        requireNotNull(address?.locality),
        address.subAdminArea,
      )
    }
  }
}
