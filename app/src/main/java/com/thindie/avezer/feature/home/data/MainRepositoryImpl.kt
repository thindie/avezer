package com.thindie.avezer.feature.home.data

import com.thindie.avezer.application.LocationResolver
import com.thindie.avezer.application.storage.Storage
import com.thindie.avezer.application.storage.StorageId
import com.thindie.avezer.application.weatherCodeRef
import com.thindie.avezer.application.weatherEmojiString
import com.thindie.avezer.engine.Log
import com.thindie.avezer.error.AppError
import com.thindie.avezer.feature.home.domain.MainRepository
import com.thindie.avezer.feature.home.domain.MockWeather
import com.thindie.avezer.feature.home.domain.Weather
import com.thindie.avezer.network.Client
import com.thindie.avezer.network.WeatherResponse
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow

class MainRepositoryImpl(
  private val storage: Storage,
  private val client: Client,
  private val locationResolver: LocationResolver,
) : MainRepository {
  private val weather =
    storage.saved.flatMapLatest { ids ->
      Log.d({ "No stored IDs found for weather flow." })

      channelFlow {
        val stored =
          coroutineScope {
            ids?.map { id ->
              async {
                try {
                  Log.d({ "Attempting to read stored ID: $id" })
                  val raw =
                    storage.read(id) ?: run {
                      Log.e(
                        { "Failed to read storage for ID: $id (null value)" },
                      )
                      return@async null
                    }
                  try {
                    JsonUtil.fromJson(raw, Weather::class.java)
                  } catch (e: Exception) {
                    Log.e({ "Error parsing JSON for ID: $id" }, throwable = e)
                    null
                  }
                } catch (e: Exception) {
                  Log.e(
                    { "Unexpected error reading/parsing storage for ID: $id" },
                    throwable = e,
                  )
                  null
                }
              }
            }?.awaitAll()
          }
            ?.filterNotNull()

        if (stored != null) {
          Log.d(
            { "Successfully processed stored weather data: $stored" },
          )
          send(stored)
        } else {
          Log.w(
            { "No valid weather data found after processing all stored IDs." },
          )
          send(null)
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
      { "Starting weather forecast fetch for ${storedIds.size} IDs." },
    )

    try {
      coroutineScope {
        val deferredResults =
          storedIds.map { id ->
            async {
              try {
                Log.d({ "Fetching and updating data for ID: $id" })
                val raw =
                  storage.read(id) ?: run {
                    Log.e(
                      { "Cannot fetch weather for ID $id: No stored data found." },
                    )
                    return@async null
                  }

                val stored =
                  try {
                    JsonUtil.fromJson(raw, Weather::class.java) ?: run {
                      Log.e({ "Failed to parse JSON for ID $id." })
                      return@async null
                    }
                  } catch (e: Exception) {
                    Log.e(
                      { "Error parsing stored weather data for ID $id." },
                      throwable = e,
                    )
                    return@async null
                  }

                // 3. Network call and update storage
                val weather =
                  client.getForecast(lat = stored.lat, lon = stored.lon).toDomainModel(stored.city)
                if (weather != null) {
                  storage.createOrUpdate(id, JsonUtil.toJson(weather))
                  Log.d(
                    { "Successfully fetched and wrote new weather data for ID $id." },
                  )
                } else {
                  Log.w(
                    { "Network call returned null domain model for ID $id." },
                  )
                }
              } catch (e: AppError.ServerError.TimeOut) {
                Log.e(
                  { "Timeout occurred while fetching weather for ID $id." },
                  throwable = e,
                )
              } catch (e: Exception) {
                Log.e(
                  { "An unexpected error occurred during fetch for ID $id." },
                  throwable = e,
                )
              }
            }
          }
        val results = deferredResults.awaitAll()
        if (results.isEmpty()) {
          Log.w(
            { "Finished fetching weather, but no successful updates were recorded." },
          )
        } else {
          Log.d(
            { "Successfully processed and updated ${results.size} weather forecasts." },
          )
        }
      }
    } catch (e: Exception) {
      Log.e(
        { "Critical error during the overall fetch process." },
        throwable = e,
      )
    }
  }

  override suspend fun read(cityName: String) {
    Log.d({ "Starting weather read for city: $cityName" })
    try {
      val location =
        locationResolver.read(cityName)
          ?: throw IllegalStateException("Location resolver failed to find coordinates for $cityName.")
      val (lat, lon) = location

      Log.d(
        { "Calling network API for $cityName at ($lat, $lon)" },
      )
      val weatherResponse = client.getForecast(lat = lat, lon = lon)
      val domainModel = weatherResponse.toDomainModel(cityName)

      if (domainModel != null) {
        val id = StorageId(cityName.lowercase())
        storage.createOrUpdate(id, JsonUtil.toJson(domainModel))
        Log.d(
          { "Successfully fetched and stored weather data for $cityName." },
        )
      } else {
        Log.w(
          { "Failed to convert network response to domain model for $cityName." },
        )
      }
    } catch (e: IllegalStateException) {
      Log.e({ "Location resolution failed for $cityName." }, throwable = e)
      throw e // Re-throw specific business logic errors
    } catch (e: AppError.ServerError.TimeOut) {
      Log.e(
        { "Timeout occurred while reading weather for $cityName." },
      )
      throw e
    } catch (e: Exception) {
      Log.e(
        { "An unexpected error occurred during read operation for $cityName." },
        throwable = e,
      )
      throw AppError.UnexpectedError(
        cause = e,
        message = e.message,
      )
    }
  }
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
