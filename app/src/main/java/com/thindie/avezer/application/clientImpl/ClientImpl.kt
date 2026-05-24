package com.thindie.avezer.application.clientImpl

import com.thindie.avezer.network.Client
import com.thindie.avezer.network.WeatherResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ClientImpl(url: String) : Client {
  private val weatherService: WeatherService = Retrofit.Builder()
    .baseUrl(url)
    .addConverterFactory(GsonConverterFactory.create())
    .client(
      OkHttpClient.Builder()
        .addInterceptor(
          HttpLoggingInterceptor()
            .apply {
              level = HttpLoggingInterceptor.Level.BODY
            }
        )
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()
    )
    .build()
    .create(WeatherService::class.java)

  override suspend fun getForecast(
    lat: Double,
    lon: Double,
    hourly: List<String>?,
    daily: List<String>?,
    forecastDays: Int,
    tempUnit: String,
  ): WeatherResponse {
    return weatherService.getForecast(
      lat = lat,
      lon = lon,
      hourly = hourly,
      daily = daily,
      forecastDays = forecastDays,
      tempUnit = tempUnit,
    )
  }
}
