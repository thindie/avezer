package com.thindie.avezer.feature.home.data

import com.thindie.avezer.application.storage.LocationResolver
import com.thindie.avezer.application.storage.Storage
import com.thindie.avezer.application.weatherCodeRef
import com.thindie.avezer.application.weatherEmojiString
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
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.cancellation.CancellationException

class MainRepositoryImpl(
  private val storage: Storage,
  private val client: Client,
  private val locationResolver: LocationResolver,
) : MainRepository {

  private val weather = storage.saved.flatMapLatest { ids ->
    flow {
      val stored = coroutineScope {
        ids?.map { id ->
          async {
            val raw = storage.read(id) ?: return@async null
            JsonUtil.fromJson(raw, Weather::class.java)
          }
        }?.awaitAll()
      }
        ?.filterNotNull()

      emit(stored)
    }
  }

  private val mockForecast = flow { emit(listOf(MockWeather.create())) }

  override val forecast: Flow<List<Weather>?> = combine(weather, mockForecast) { w, m ->
    (w ?: emptyList()) + m
  }


  override suspend fun fetch() {
    coroutineScope {
      storage.saved.firstOrNull()?.map { id ->
        async {
          val raw = storage.read(id) ?: return@async null
          val stored = JsonUtil.fromJson(raw, Weather::class.java) ?: return@async null
          readInternal {
            val weather =
              client.getForecast(lat = stored.lat, lon = stored.lon).toDomainModel(stored.city)
            storage.write(JsonUtil.toJson(weather))
          }
        }
      }
    }?.awaitAll()
  }


  override suspend fun read(cityName: String) {
    val (lat, lon) = locationResolver.read(cityName) ?: (52.0 to 53.0)
     readInternal {
      val weather = client.getForecast(lat = lat, lon = lon)
      storage.write(JsonUtil.toJson(weather.toDomainModel(cityName)))
    }
  }


  private suspend fun readInternal(block: suspend () -> Unit) =
    withTimeoutOrNull(5_000L) {
      try {
        block.invoke()
      } catch (e: CancellationException) {
        throw e
      } catch (_: HttpRequestTimeoutException) {
        throw AppError.ServerError.TimeOut
      }
    } ?: throw AppError.ServerError.TimeOut
}

fun WeatherResponse.toDomainModel(
  cityName: String,
): Weather? {
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
