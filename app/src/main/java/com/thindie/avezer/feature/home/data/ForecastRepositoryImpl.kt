package com.thindie.avezer.feature.home.data

import com.thindie.avezer.application.storage.Storage
import com.thindie.avezer.application.storage.StorageId
import com.thindie.avezer.application.weatherCodeRef
import com.thindie.avezer.application.weatherEmojiString
import com.thindie.avezer.engine.Log
import com.thindie.avezer.feature.home.domain.DailyForecast
import com.thindie.avezer.feature.home.domain.FavoriteLocation
import com.thindie.avezer.feature.home.domain.ForecastRepository
import com.thindie.avezer.feature.home.domain.HourlyForecast
import com.thindie.avezer.feature.home.domain.Weather
import com.thindie.avezer.network.Client
import com.thindie.avezer.network.WeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withTimeoutOrNull
import java.util.Locale

class ForecastRepositoryImpl(
  private val storage: Storage,
  private val client: Client,
) : ForecastRepository {
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

    val cachedLocations = knownWeatherKeys().ifEmpty { return }
    supervisorScope {
      for (location in cachedLocations) {
        launch {
          val weather = readFromStorageInternal(location.storageId)
          if (weather != null) {
            weatherCache.update {
              val map = it.orEmpty().toMutableMap()
              map[location.storageId] = weather
              map.toMap()
            }
          }
        }
      }
    }
    supervisorScope {
      for (location in cachedLocations) {
        launch {
          Log.d({ "Starting weather fetch: $location" })
          val weather =
            withTimeoutOrNull(1600L) {
              client.getForecast(lat = location.lat, lon = location.lon)
                .toDomainModel(location.city)
            }
          Log.d({ "Fetch for $location == $weather" })
          if (weather != null) {
            storeWeather(weather)
          }
        }
      }
    }
  }

  override suspend fun read(
    lat: Double,
    lon: Double,
    cityName: String,
  ): Weather {
    Log.d({ "Starting weather read for city: $cityName" })
    val weatherResponse = client.getForecast(lat = lat, lon = lon)
    val domainModel = weatherResponse.toDomainModel(cityName)
    return requireNotNull(domainModel)
  }

  private suspend fun storeWeather(weather: Weather) {
    val id = weatherKey(FavoriteLocation(weather.city, weather.lat, weather.lon))
    storage.createOrUpdate(id, JsonUtil.toJson(weather))
    Log.d({ "Write successful. Key: ${id.value}" })
    val weather = readFromStorageInternal(id)
    if (weather != null) {
      weatherCache.update { previous ->
        val updated = (previous ?: emptyMap()).toMutableMap()
        updated[id] = weather
        updated
      }
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

    val allSavedIds = storage.saved.firstOrNull() ?: return fromFavorites
    val fromStorage =
      allSavedIds.mapNotNull { id: StorageId ->
        if (id.value.startsWith(WEATHER_KEY_PREFIX)) parseStorageKey(id) else null
      }

    return (fromFavorites + fromStorage)
      .distinctBy { it.storageId.value }
  }

  private suspend fun favoriteLocations(): List<FavoriteLocation> {
    return storage.read(favoriteStorageId)
      ?.split(FAVORITE_PLACES_SEPARATOR)
      ?.mapNotNull { JsonUtil.fromJson(it, FavoriteLocation::class.java) }
      .orEmpty()
  }

  private fun parseStorageKey(id: StorageId): CachedWeatherLocation? {
    if (!id.value.startsWith(WEATHER_KEY_PREFIX)) return null

    val parts = id.value.removePrefix(WEATHER_KEY_PREFIX).split(KEY_SEPARATOR)
    if (parts.size != KEY_PART_COUNT) {
      Log.w({ "Invalid weather storage key: ${id.value}" })
      return null
    }

    val city = parts.first().ifBlank { null } ?: return null
    val lat =
      parts.getOrNull(1)?.toDoubleOrNull() ?: return null
    val lon =
      parts.getOrNull(2)?.toDoubleOrNull() ?: return null

    return CachedWeatherLocation(
      storageId = id,
      city = city,
      lat = lat,
      lon = lon,
    )
  }

  private suspend fun readFromStorageInternal(id: StorageId): Weather? {
    val raw = storage.read(id).orEmpty()
    val weather = JsonUtil.fromJson(raw, Weather::class.java)
    return weather
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
}

internal fun formatCoordinate(value: Double): String {
  return "%.1f".format(Locale.US, value)
}
