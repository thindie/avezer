package com.thindie.avezer.application.storage

import android.content.Context
import android.location.Geocoder
import com.thindie.avezer.application.Address
import com.thindie.avezer.application.LocationResolver
import com.thindie.avezer.engine.Log
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

class LocationResolverImpl(
  private val context: Context,
) : LocationResolver {
  private val geocoder by lazy { Geocoder(context, Locale.getDefault()) }

  override suspend fun read(name: String): Pair<Double, Double>? {
    return try {
      withContext(Dispatchers.IO) {
        Log.d({ "Attempting to resolve location name for: $name" })

        val addresses = geocoder.getFromLocationName(name, 1)
        if (addresses == null || addresses.isEmpty()) {
          Log.w({ "No addresses found for name: $name" })
          return@withContext null
        }
        val result =
          addresses.firstOrNull()?.let {
            Log.d({
              "Found location: ${it.locality}, Lat: ${it.latitude}, Lon: ${it.longitude}"
            })
            it.latitude to it.longitude
          }
        result
      }
    } catch (e: CancellationException) {
      throw e
    } catch (e: Throwable) {
      TODO("Nominatim fallback")
    }
  }

  override suspend fun readAddresses(name: String): List<Address> {
    return try {
      withContext(Dispatchers.IO) {
        Log.d(
          { "Attempting to resolve address for name: ($name)" },
        )
        val addresses =
          geocoder.getFromLocationName(
            // locationName =
            name,
            // maxResults =
            10,
          )

        if (addresses.isNullOrEmpty()) {
          Log.w(
            { "No address found for name: ($name)" },
          )
          return@withContext emptyList()
        }
        return@withContext addresses.mapNotNull {
          Address(
            it.locality,
            it.subAdminArea,
          )
        }
      }
    } catch (e: CancellationException) {
      throw e
    } catch (e: Throwable) {
      TODO("Nominatim fallback")
    }
  }

  override suspend fun read(
    lat: Double,
    lon: Double,
  ): Address? {
    return try {
      withContext(Dispatchers.IO) {
        Log.d(
          { "Attempting to resolve address for coordinates: ($lat, $lon)" },
        )
        val addresses = geocoder.getFromLocation(lat, lon, 1)
        if (addresses.isNullOrEmpty()) {
          Log.w(
            { "No address found for coordinates: ($lat, $lon)" },
          )
          return@withContext null
        }
        val address = addresses.firstOrNull()
        Log.d(
          { "Found address: Locality=${address?.locality}, SubAdminArea=${address?.subAdminArea}" },
        )
        return@withContext Address(
          requireNotNull(address?.locality),
          address.subAdminArea,
        )
      }
    } catch (e: CancellationException) {
      throw e
    } catch (e: Throwable) {
      TODO("Nominatim fallback")
    }
  }
}
