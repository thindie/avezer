package com.thindie.avezer.application.storage

import android.content.Context
import android.location.Geocoder
import com.thindie.avezer.engine.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.util.Locale

class LocationResolverImpl(private val context: Context) : LocationResolver {
  private val geocoder by lazy { Geocoder(context, Locale.getDefault()) }

  override suspend fun read(name: String): Pair<Double, Double>? {
    return withContext(Dispatchers.IO) {
      Log.d("[avezer]: LocationResolverImpl", "Attempting to resolve location name for: $name")

      val addresses = geocoder.getFromLocationName(name, 1)
      if (addresses == null || addresses.isEmpty()) {
        Log.w("[avezer]: LocationResolverImpl", "No addresses found for name: $name")
        return@withContext null
      }
      val result = addresses.firstOrNull()?.let {
        Log.d(
          "[avezer]: LocationResolverImpl",
          "Found location: ${it.locality}, Lat: ${it.latitude}, Lon: ${it.longitude}"
        )
        it.latitude to it.longitude
      }
      result
    }
  }

  override suspend fun read(
    lat: Double,
    lon: Double,
  ): Address? {
    return withContext(Dispatchers.IO) {
      Log.d("[avezer]: LocationResolverImpl", "Attempting to resolve address for coordinates: ($lat, $lon)")
      val addresses = geocoder.getFromLocation(lat, lon, 1)
      if (addresses == null || addresses.isEmpty()) {
        Log.w("[avezer]: LocationResolverImpl", "No address found for coordinates: ($lat, $lon)")
        return@withContext null
      }
      val address = addresses.firstOrNull()
      Log.d(
        "[avezer]: LocationResolverImpl",
        "Found address: Locality=${address?.locality}, SubAdminArea=${address?.subAdminArea}"
      )
      return@withContext Address(
        requireNotNull(address?.locality),
        address.subAdminArea,
      )
    }
  }
}
