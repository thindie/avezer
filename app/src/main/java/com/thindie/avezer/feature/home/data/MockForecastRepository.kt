package com.thindie.avezer.feature.home.data

import com.thindie.avezer.R
import com.thindie.avezer.feature.home.domain.DailyForecast
import com.thindie.avezer.feature.home.domain.ForecastRepository
import com.thindie.avezer.feature.home.domain.HourlyForecast
import com.thindie.avezer.feature.home.domain.Weather
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class MockForecastRepository : ForecastRepository {
  private val _forecast = MutableStateFlow<List<Weather>?>(null)

  override val forecast: Flow<List<Weather>?> = _forecast

  init {
    _forecast.value = mockCities()
  }

  override suspend fun fetch(useCacheOnly: Boolean) {
    // Mock data is already available, nothing to do.
  }

  override suspend fun read(
    lat: Double,
    lon: Double,
    cityName: String,
  ): Weather {
    return mockCities().firstOrNull { it.city == cityName }
      ?: mockCities().first()
  }

  private fun mockCities(): List<Weather> =
    listOf(
      moscowMock(),
      saintPetersburgMock(),
      yekaterinburgMock(),
      novosibirskMock(),
      krasnodarMock(),
    )

  private fun moscowMock() =
    Weather(
      lat = 55.7558,
      lon = 37.6173,
      city = "Moscow",
      temperature = 24.0,
      isDay = true,
      weatherCodeRef = R.string.weather_code_0,
      emoji = "\u2600\uFE0F",
      humidity = 45,
      windSpeed = 12.3,
      lastUpdated = System.currentTimeMillis(),
      timezone = "Europe/Moscow",
      timezoneAbbreviation = "MSK",
      utcOffsetSeconds = 10800,
      forecast = mockDailyForecast(),
      hourlyForecast = mockHourlyForecast(),
    )

  private fun saintPetersburgMock() =
    Weather(
      lat = 59.9343,
      lon = 30.3351,
      city = "Saint Petersburg",
      temperature = 18.2,
      isDay = true,
      weatherCodeRef = R.string.weather_code_2,
      emoji = "\u2601\uFE0F",
      humidity = 62,
      windSpeed = 8.7,
      lastUpdated = System.currentTimeMillis(),
      timezone = "Europe/Moscow",
      timezoneAbbreviation = "MSK",
      utcOffsetSeconds = 10800,
      forecast = mockDailyForecast(),
      hourlyForecast = mockHourlyForecast(),
    )

  private fun yekaterinburgMock() =
    Weather(
      lat = 56.8389,
      lon = 60.6057,
      city = "Yekaterinburg",
      temperature = 15.8,
      isDay = false,
      weatherCodeRef = R.string.weather_code_63,
      emoji = "\uD83C\uDF27\uFE0F",
      humidity = 78,
      windSpeed = 15.2,
      lastUpdated = System.currentTimeMillis(),
      timezone = "Asia/Yekaterinburg",
      timezoneAbbreviation = "YEKT",
      utcOffsetSeconds = 14400,
      forecast = mockDailyForecast(),
      hourlyForecast = mockHourlyForecast(),
    )

  private fun novosibirskMock() =
    Weather(
      lat = 55.0084,
      lon = 82.9357,
      city = "Novosibirsk",
      temperature = 19.5,
      isDay = true,
      weatherCodeRef = R.string.weather_code_61,
      emoji = "\uD83C\uDF26\uFE0F",
      humidity = 70,
      windSpeed = 9.4,
      lastUpdated = System.currentTimeMillis(),
      timezone = "Asia/Novosibirsk",
      timezoneAbbreviation = "NOVT",
      utcOffsetSeconds = 21600,
      forecast = mockDailyForecast(),
      hourlyForecast = mockHourlyForecast(),
    )

  private fun krasnodarMock() =
    Weather(
      lat = 45.0393,
      lon = 38.9764,
      city = "Krasnodar",
      temperature = 31.2,
      isDay = true,
      weatherCodeRef = R.string.weather_code_0,
      emoji = "\u2600\uFE0F",
      humidity = 35,
      windSpeed = 5.1,
      lastUpdated = System.currentTimeMillis(),
      timezone = "Europe/Moscow",
      timezoneAbbreviation = "MSK",
      utcOffsetSeconds = 10800,
      forecast = mockDailyForecast(),
      hourlyForecast = mockHourlyForecast(),
    )

  private fun mockDailyForecast(): List<DailyForecast> =
    listOf(
      DailyForecast(
        time = "2025-07-06",
        temperatureMax = 28.0,
        temperatureMin = 19.0,
        weatherCodeRef = R.string.weather_code_0,
        emoji = "\u2600\uFE0F",
        sunrise = null,
        sunset = null,
        precipitationSum = 0.0,
      ),
      DailyForecast(
        time = "2025-07-07",
        temperatureMax = 26.5,
        temperatureMin = 18.3,
        weatherCodeRef = R.string.weather_code_2,
        emoji = "\u2601\uFE0F",
        sunrise = null,
        sunset = null,
        precipitationSum = 2.5,
      ),
      DailyForecast(
        time = "2025-07-08",
        temperatureMax = 22.1,
        temperatureMin = 16.8,
        weatherCodeRef = R.string.weather_code_63,
        emoji = "\uD83C\uDF27\uFE0F",
        sunrise = null,
        sunset = null,
        precipitationSum = 8.3,
      ),
      DailyForecast(
        time = "2025-07-09",
        temperatureMax = 20.0,
        temperatureMin = 14.5,
        weatherCodeRef = R.string.weather_code_61,
        emoji = "\uD83C\uDF26\uFE0F",
        sunrise = null,
        sunset = null,
        precipitationSum = 5.1,
      ),
      DailyForecast(
        time = "2025-07-10",
        temperatureMax = 23.8,
        temperatureMin = 15.2,
        weatherCodeRef = R.string.weather_code_3,
        emoji = "\u2600\uFE0F",
        sunrise = null,
        sunset = null,
        precipitationSum = 0.0,
      ),
    )

  private fun mockHourlyForecast(): List<HourlyForecast> =
    listOf(
      HourlyForecast(
        time = "2025-07-06T12:00",
        temperature = 24.5,
        humidity = 45,
        windSpeed = 8.3,
        precipitation = 0.0,
        weatherCodeRef = R.string.weather_code_0,
        emoji = "\u2600\uFE0F",
      ),
      HourlyForecast(
        time = "2025-07-06T15:00",
        temperature = 26.1,
        humidity = 40,
        windSpeed = 10.1,
        precipitation = 0.0,
        weatherCodeRef = R.string.weather_code_0,
        emoji = "\u2600\uFE0F",
      ),
      HourlyForecast(
        time = "2025-07-06T18:00",
        temperature = 22.3,
        humidity = 55,
        windSpeed = 6.7,
        precipitation = 0.5,
        weatherCodeRef = R.string.weather_code_2,
        emoji = "\u2601\uFE0F",
      ),
      HourlyForecast(
        time = "2025-07-06T21:00",
        temperature = 18.9,
        humidity = 68,
        windSpeed = 4.2,
        precipitation = 2.3,
        weatherCodeRef = R.string.weather_code_63,
        emoji = "\uD83C\uDF27\uFE0F",
      ),
      HourlyForecast(
        time = "2025-07-07T00:00",
        temperature = 17.1,
        humidity = 72,
        windSpeed = 3.8,
        precipitation = 1.1,
        weatherCodeRef = R.string.weather_code_61,
        emoji = "\uD83C\uDF26\uFE0F",
      ),
      HourlyForecast(
        time = "2025-07-07T03:00",
        temperature = 16.5,
        humidity = 75,
        windSpeed = 3.1,
        precipitation = 0.8,
        weatherCodeRef = R.string.weather_code_61,
        emoji = "\uD83C\uDF26\uFE0F",
      ),
      HourlyForecast(
        time = "2025-07-07T06:00",
        temperature = 17.8,
        humidity = 70,
        windSpeed = 4.5,
        precipitation = 0.3,
        weatherCodeRef = R.string.weather_code_2,
        emoji = "\u2601\uFE0F",
      ),
      HourlyForecast(
        time = "2025-07-07T09:00",
        temperature = 20.4,
        humidity = 58,
        windSpeed = 6.2,
        precipitation = 0.0,
        weatherCodeRef = R.string.weather_code_3,
        emoji = "\u2600\uFE0F",
      ),
    )
}
