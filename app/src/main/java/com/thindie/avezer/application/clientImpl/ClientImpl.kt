package com.thindie.avezer.application.clientImpl

import com.thindie.avezer.network.Client
import com.thindie.avezer.network.WeatherResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class ClientImpl(val url: String): Client {
  private val client = HttpClient {
    install(ContentNegotiation) {
      json(Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        prettyPrint = false
      })
    }
  }

  override suspend fun getForecast(
    lat: Double,
    lon: Double,
    hourly: List<String>?,
    daily: List<String>?,
    forecastDays: Int,
    tempUnit: String
  ): WeatherResponse {
    return withContext(Dispatchers.IO) {
      try {
        client.get(url) {
          val builder = this
          parameter("latitude", lat)
          parameter("longitude", lon)
          hourly?.let { builder.parameter("hourly", it.joinToString(",")) }
          daily?.let { builder.parameter("daily", it.joinToString(",")) }
          parameter("forecast_days", forecastDays)
          parameter("temperature_unit", tempUnit)
        }.body()
      } catch (e: CancellationException) {
        throw e
      } catch (e: Exception) {
        e.printStackTrace()
        null
      }
    } ?: run {
      throw RuntimeException("Failed to fetch weather forecast")
    }
  }
}

