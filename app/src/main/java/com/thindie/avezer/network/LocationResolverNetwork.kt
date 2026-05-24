package com.thindie.avezer.network

import com.thindie.avezer.application.Address
import com.thindie.avezer.application.LocationResolver
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class LocationResolverNetwork : LocationResolver {

  private val client =
    HttpClient {
      install(ContentNegotiation) {
        json(
          Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
            prettyPrint = false
          },
        )
      }
    }

  override suspend fun read(name: String): Pair<Double, Double>? {
    return null
  }

  override suspend fun read(
    lat: Double,
    lon: Double,
  ): Address? {
    return null
  }
}


private const val BASE_URL = "https://nominatim.openstreetmap.org/search"
