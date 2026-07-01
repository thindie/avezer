package com.thindie.avezer.feature.home.data

import android.os.Build
import androidx.annotation.RequiresApi
import com.thindie.avezer.application.LocationResolver
import com.thindie.avezer.application.storage.Storage
import com.thindie.avezer.application.storage.StorageId
import com.thindie.avezer.application.weatherCodeRef
import com.thindie.avezer.application.weatherEmojiString
import com.thindie.avezer.engine.Log
import com.thindie.avezer.error.AppError
import com.thindie.avezer.feature.home.domain.DailyForecast
import com.thindie.avezer.feature.home.domain.HourlyForecast
import com.thindie.avezer.feature.home.domain.MainRepository
import com.thindie.avezer.feature.home.domain.Weather
import com.thindie.avezer.network.Client
import com.thindie.avezer.network.WeatherResponse
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest

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
          if (ids == null) {
            null
          } else {
            coroutineScope {
              ids.map { id -> async { readStoredWeather(id) } }
                .awaitAll()
                .filterNotNull()
            }
          }

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

  override val forecast: Flow<List<Weather>?> = weather

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

                val stored =
                  readStoredWeather(id) ?: run {
                    return@async null
                  }

                // 3. Network call and update storage
                val weather =
                  client.getForecast(lat = stored.lat, lon = stored.lon).toDomainModel(stored.city)
                if (weather != null) {
                  val createRaw = JsonUtil.toJson(weather)
                  Log.d(
                    { "Created Raw weather: $createRaw" },
                  )
                  storage.createOrUpdate(id, createRaw)
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
        val successfulUpdates = results.count { it != null }

        if (successfulUpdates == 0) {
          Log.w(
            { "Finished fetching weather, but no forecasts were successfully updated." },
          )
        } else {
          Log.d(
            { "Successfully fetched and updated $successfulUpdates weather forecasts." },
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
      readInternal(lat, lon, cityName)
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

  override suspend fun read(
    lat: Double,
    lon: Double,
    cityName: String,
  ) {
    Log.d({ "Starting weather read for city: $cityName" })
    try {
      readInternal(lat, lon, cityName)
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

  private suspend fun readInternal(
    lat: Double,
    lon: Double,
    cityName: String,
  ) {
    Log.d(
      { "Calling network API at ($lat, $lon)" },
    )
    val weatherResponse: WeatherResponse = client.getForecast(lat = lat, lon = lon)
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
  }

  private suspend fun readStoredWeather(id: StorageId): Weather? {
    return try {
      val raw =
        storage.read(id) ?: run {
          Log.e({ "Failed to read weather for ID $id." })
          null
        }

      if (raw == null) {
        null
      } else {
        JsonUtil.fromJson(raw, Weather::class.java)
      }
    } catch (e: Exception) {
      Log.e(
        { "Error reading or parsing weather data for ID $id." },
        throwable = e,
      )
      null
    }
  }
}

@RequiresApi(Build.VERSION_CODES.O)
fun WeatherResponse.toDomainModel(cityName: String): Weather? {
  val currentData = this.current
  val hourlyData = this.hourly
  val dailyData = this.daily
  val code = currentData.weather_code

  // Map hourly forecast data
  val hourlyForecast =
    hourlyData.time.mapIndexed { index, time ->
      HourlyForecast(
        time = time,
        temperature = hourlyData.temperature_2m[index],
        humidity = hourlyData.relative_humidity_2m[index],
        windSpeed = hourlyData.wind_speed_10m[index],
        precipitation = hourlyData.precipitation[index],
        weatherCodeRef = weatherCodeRef(hourlyData.weather_code[index]),
        emoji = weatherEmojiString(hourlyData.weather_code[index]),
      )
    }

  // Map daily forecast data
  val forecast =
    dailyData.time.mapIndexed { index, string ->
      DailyForecast(
        time = string,
        temperatureMax = dailyData.temperature_2m_max[index],
        temperatureMin = dailyData.temperature_2m_min[index],
        weatherCodeRef = weatherCodeRef(dailyData.weather_code[index]),
        emoji = weatherEmojiString(dailyData.weather_code[index]),
        sunrise = dailyData.sunrise[index],
        sunset = dailyData.sunset[index],
        precipitationSum = dailyData.precipitation_sum[index],
      )
    }

  return Weather(
    city = cityName,
    temperature = currentData.temperature_2m,
    isDay = currentData.is_day == 1,
    weatherCodeRef = weatherCodeRef(code),
    emoji = weatherEmojiString(code),
    humidity = hourlyData.relative_humidity_2m[0],
    windSpeed = currentData.wind_speed_10m,
    lat = latitude,
    lon = longitude,
    timezone = timezone,
    timezoneAbbreviation = timezone_abbreviation,
    utcOffsetSeconds = utc_offset_seconds,
    forecast = forecast,
    hourlyForecast = hourlyForecast,
  )
}
