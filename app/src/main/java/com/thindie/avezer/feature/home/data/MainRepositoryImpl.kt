package com.thindie.avezer.feature.home.data

import com.thindie.avezer.application.storage.LocationResolver
import com.thindie.avezer.application.storage.Storage
import com.thindie.avezer.application.weatherCodeRef
import com.thindie.avezer.application.weatherEmojiString
import com.thindie.avezer.engine.Log
import com.thindie.avezer.error.AppError
import com.thindie.avezer.feature.home.domain.MainRepository
import com.thindie.avezer.feature.home.domain.MockWeather
import com.thindie.avezer.feature.home.domain.Weather
import com.thindie.avezer.network.Client
import com.thindie.avezer.network.WeatherResponse
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.cancellation.CancellationException

class MainRepositoryImpl(
  private val storage: Storage,
  private val client: Client,
  private val locationResolver: LocationResolver,
) : MainRepository {
  private val weather =
    storage.saved.flatMapLatest { ids ->
      Log.d("[avezer]: MainRepositoryImpl", "No stored IDs found for weather flow.")


      channelFlow {
        val stored =
          coroutineScope {
            ids?.map { id ->
              async {
                try {
                  Log.d("[avezer]: MainRepositoryImpl", "Attempting to read stored ID: $id")
                  val raw = storage.read(id) ?: run {
                    Log.e(
                      "[avezer]: MainRepositoryImpl",
                      "Failed to read storage for ID: $id (null value)"
                    )
                    return@async null
                  }
                  try {
                    JsonUtil.fromJson(raw, Weather::class.java)
                  } catch (e: Exception) {
                    Log.e("[avezer]: MainRepositoryImpl", "Error parsing JSON for ID: $id", e)
                    null
                  }
                } catch (e: Exception) {
                  Log.e(
                    "[avezer]: MainRepositoryImpl",
                    "Unexpected error reading/parsing storage for ID: $id",
                    e
                  )
                  null
                }
              }
            }?.awaitAll()
          }
            ?.filterNotNull()

        if (stored != null) {
          Log.d("[avezer]: MainRepositoryImpl", "Successfully processed stored weather data: $stored")
          send(stored)
        } else {
          Log.w(
            "[avezer]: MainRepositoryImpl",
            "No valid weather data found after processing all stored IDs."
          )
          send(null) // Signal no real data is available
        }
      }
    }

  private val mockForecast = flow { emit(listOf(MockWeather.create())) }

  override val forecast: Flow<List<Weather>?> =
    combine(weather, mockForecast) { weather, mock ->
      weather ?: mock
    }

  override suspend fun fetch() {
    val storedIds = storage.saved.firstOrNull() ?: return
    Log.d(
      "[avezer]: MainRepositoryImpl",
      "Starting weather forecast fetch for ${storedIds.size} IDs."
    )

    try {
      coroutineScope {
        val deferredResults = storedIds.map { id ->
          async {
            try {
              Log.d("[avezer]: MainRepositoryImpl", "Fetching and updating data for ID: $id")
              // 1. Read existing data
              val raw = storage.read(id) ?: run {
                Log.e(
                  "[avezer]: MainRepositoryImpl",
                  "Cannot fetch weather for ID $id: No stored data found."
                )
                return@async null
              }

              // 2. Parse and convert to domain model
              val stored = try {
                JsonUtil.fromJson(raw, Weather::class.java) ?: run {
                  Log.e("[avezer]: MainRepositoryImpl", "Failed to parse JSON for ID $id.")
                  return@async null
                }
              } catch (e: Exception) {
                Log.e(
                  "[avezer]: MainRepositoryImpl",
                  "Error parsing stored weather data for ID $id.",
                  e
                )
                return@async null
              }

              // 3. Network call and update storage
              readInternal {
                val weather =
                  client.getForecast(lat = stored.lat, lon = stored.lon).toDomainModel(stored.city)
                if (weather != null) {
                  storage.write(JsonUtil.toJson(weather))
                  Log.d(
                    "[avezer]: MainRepositoryImpl",
                    "Successfully fetched and wrote new weather data for ID $id."
                  )
                } else {
                  Log.w(
                    "[avezer]: MainRepositoryImpl",
                    "Network call returned null domain model for ID $id."
                  )
                }
              }
            } catch (e: AppError.ServerError.TimeOut) {
              Log.e(
                "[avezer]: MainRepositoryImpl",
                "Timeout occurred while fetching weather for ID $id.",
                e
              )
            } catch (e: Exception) {
              Log.e(
                "[avezer]: MainRepositoryImpl",
                "An unexpected error occurred during fetch for ID $id.",
                e
              )
            }
          }
        }
        val results = deferredResults.awaitAll()
        if (results.isEmpty()) {
          Log.w(
            "[avezer]: MainRepositoryImpl",
            "Finished fetching weather, but no successful updates were recorded."
          )
        } else {
          Log.d(
            "[avezer]: MainRepositoryImpl",
            "Successfully processed and updated ${results.size} weather forecasts."
          )
        }
      }
    } catch (e: Exception) {
      Log.e("[avezer]: MainRepositoryImpl", "Critical error during the overall fetch process.", e)
    }
  }

  override suspend fun read(cityName: String) {
    Log.d("[avezer]: MainRepositoryImpl", "Starting weather read for city: $cityName")
    try {
      val location = locationResolver.read(cityName)
      if (location == null) {
        throw IllegalStateException("Location resolver failed to find coordinates for $cityName.")
      }
      val (lat, lon) = location

      readInternal {
        Log.d("[avezer]: MainRepositoryImpl", "Calling network API for $cityName at ($lat, $lon)")
        val weatherResponse = client.getForecast(lat = lat, lon = lon)
        val domainModel = weatherResponse.toDomainModel(cityName)

        if (domainModel != null) {
          storage.write(JsonUtil.toJson(domainModel))
          Log.d(
            "[avezer]: MainRepositoryImpl",
            "Successfully fetched and stored weather data for $cityName."
          )
        } else {
          Log.w(
            "[avezer]: MainRepositoryImpl",
            "Failed to convert network response to domain model for $cityName."
          )
        }
      }
    } catch (e: IllegalStateException) {
      Log.e("[avezer]: MainRepositoryImpl", "Location resolution failed for $cityName.", e)
      throw e // Re-throw specific business logic errors
    } catch (e: AppError.ServerError.TimeOut) {
      Log.e("[avezer]: MainRepositoryImpl", "Timeout occurred while reading weather for $cityName.")
      throw e
    } catch (e: Exception) {
      Log.e(
        "[avezer]: MainRepositoryImpl",
        "An unexpected error occurred during read operation for $cityName.",
        e
      )
      throw AppError.UnexpectedError(
        cause = e,
        message = e.message
      ) // Or throw a more specific exception
    }
  }

  private suspend fun readInternal(block: suspend () -> Unit) =
    withTimeoutOrNull(5_000L) {
      try {
        block.invoke()
        Log.d("[avezer]: MainRepositoryImpl", "readInternal block executed successfully.")
      } catch (e: CancellationException) {
        Log.w("[avezer]: MainRepositoryImpl", "Operation cancelled by coroutine scope.")
        throw e
      } catch (_: HttpRequestTimeoutException) {
        Log.e("[avezer]: MainRepositoryImpl", "Network request timed out after 5 seconds.")
        throw AppError.ServerError.TimeOut
      } catch (e: Exception) {
        Log.e(
          "[avezer]: MainRepositoryImpl",
          "An unexpected error occurred in readInternal block.",
          e
        )
        throw e // Re-throw to be caught by the caller's try/catch
      }
    } ?: throw AppError.ServerError.TimeOut
}

fun WeatherResponse.toDomainModel(cityName: String): Weather? {
  val current = this.current
  val hourlyData = this.hourly
  val code = current?.weatherCode ?: 1000
  val lat = latitude ?: return null
  val lng = longitude ?: return null

  return Weather(
    city = cityName,
    temperature = current?.temperature ?: 0.0,
    isDay = hourlyData?.isDay == 1,
    weatherCodeRef = weatherCodeRef(code),
    emoji = weatherEmojiString(code),
    humidity = null,
    windSpeed = current?.windSpeed,
    lat = lat,
    lon = lng,
  )
}
