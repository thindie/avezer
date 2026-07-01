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
import com.thindie.avezer.feature.home.domain.FavoriteLocation
import com.thindie.avezer.feature.home.domain.HourlyForecast
import com.thindie.avezer.feature.home.domain.MainRepository
import com.thindie.avezer.feature.home.domain.Weather
import com.thindie.avezer.network.Client
import com.thindie.avezer.network.WeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.util.Locale

class MainRepositoryImpl(
  private val storage: Storage,
  private val client: Client,
  private val locationResolver: LocationResolver,
) : MainRepository {
  private data class CachedWeatherLocation(
    val storageId: StorageId,
    val city: String,
    val lat: Double,
    val lon: Double,
  )

  private val weatherCache = MutableStateFlow<Map<StorageId, Weather>?>(null)

  override val forecast: Flow<List<Weather>?> =
    weatherCache.map { it?.values.orEmpty().toList() }

  override suspend fun fetch() {
    Log.d({ "Starting weather cache refresh." })

    val cachedLocations = knownWeatherKeys()
    if (cachedLocations.isEmpty()) {
      Log.w({ "Finished fetching weather, but no cached locations were found." })
      return
    }

    try {
      for (location in cachedLocations) {
        updateCachedWeather(location)
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
      throw e
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
      throw e
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
    Log.d({ "Calling network API at ($lat, $lon)" })
    val weatherResponse = client.getForecast(lat = lat, lon = lon)
    val domainModel = weatherResponse.toDomainModel(cityName)

    if (domainModel != null) {
      storeWeather(domainModel)
      Log.d(
        { "Successfully fetched and stored weather data for $cityName." },
      )
    } else {
      Log.w(
        { "Failed to convert network response to domain model for $cityName." },
      )
    }
  }

  private suspend fun updateCachedWeather(location: CachedWeatherLocation): Boolean {
    try {
      Log.d({ "Fetching and updating data for ID: ${location.storageId.value}" })

      val weather = client.getForecast(lat = location.lat, lon = location.lon).toDomainModel(location.city)
      if (weather != null) {
        storeWeather(weather)
        Log.d(
          { "Successfully fetched and wrote new weather data for ID ${location.storageId.value}." },
        )
      } else {
        Log.w({ "Network call returned null domain model for location ${location.city}." })
      }

      return true
    } catch (e: AppError.ServerError.TimeOut) {
      Log.e(
        { "Timeout occurred while fetching weather for ID ${location.storageId.value}." },
        throwable = e,
      )
      return false
    } catch (e: Exception) {
      Log.e(
        { "An unexpected error occurred during fetch for location ${location.city}." },
        throwable = e,
      )
      return false
    }
  }

  private suspend fun storeWeather(weather: Weather) {
    val id = weatherKey(FavoriteLocation(weather.city, weather.lat, weather.lon))
    storage.createOrUpdate(id, JsonUtil.toJson(weather))
    Log.d({ "Write successful. Key: ${id.value}" })
    weatherCache.update { previous ->
      val updated = (previous ?: emptyMap()).toMutableMap()
      updated[id] = weather
      updated
    }
  }

  private suspend fun knownWeatherKeys(): List<CachedWeatherLocation> {
    val fromFavorites =
      favoriteLocations().map { location ->
        CachedWeatherLocation(
          storageId = weatherKey(location),
          city = location.city,
          lat = location.lat,
          lon = location.lon,
        )
      }

    val allSavedIds =
      try {
        storage.saved.first()
      } catch (e: Exception) {
        null
      }
    if (allSavedIds == null) return fromFavorites
    val fromStorage =
      allSavedIds.mapNotNull { id: StorageId ->
        if (id.value.startsWith(WEATHER_KEY_PREFIX)) parseStorageKey(id) else null
      }

    return (fromFavorites + fromStorage)
      .distinctBy { it.storageId.value }
  }

  private suspend fun favoriteLocations(): List<FavoriteLocation> {
    return FavoriteLocationStorage.read(storage, locationResolver)
  }

  private fun parseStorageKey(id: StorageId): CachedWeatherLocation? {
    if (!id.value.startsWith(WEATHER_KEY_PREFIX)) return null

    val parts = id.value.removePrefix(WEATHER_KEY_PREFIX).split(KEY_SEPARATOR)
    if (parts.size != KEY_PART_COUNT) {
      Log.w({ "Invalid weather storage key: ${id.value}" })
      return null
    }

    val city = parts.first()
    val lat =
      parts.getOrNull(1)?.toDoubleOrNull() ?: run {
        Log.w(
          {
            "Invalid latitude in weather storage key: ${id.value}"
          },
        )
        return null
      }
    val lon =
      parts.getOrNull(2)?.toDoubleOrNull() ?: run {
        Log.w(
          {
            "Invalid longitude in weather storage key: ${id.value}"
          },
        )
        return null
      }

    if (city.isBlank()) {
      Log.w(
        {
          "Empty city in weather storage key: ${id.value}"
        },
      )
      return null
    }

    return CachedWeatherLocation(
      storageId = id,
      city = city,
      lat = lat,
      lon = lon,
    )
  }

  private fun weatherKey(location: FavoriteLocation): StorageId {
    return StorageId(
      listOf(
        WEATHER_KEY_PREFIX,
        location.city.lowercase(Locale.US),
        formatCoordinate(location.lat),
        formatCoordinate(location.lon),
      ).joinToString(KEY_SEPARATOR),
    )
  }

  companion object {
    private const val KEY_SEPARATOR = "|"
    private const val KEY_PART_COUNT = 4
    private const val WEATHER_KEY_PREFIX = "id_key_###weather"
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

  private fun formatCoordinate(value: Double): String {
    return "%.6f".format(Locale.US, value)
  }
}
