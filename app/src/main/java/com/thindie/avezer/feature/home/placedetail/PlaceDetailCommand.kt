package com.thindie.avezer.feature.home.placedetail

import com.thindie.avezer.engine.Command
import com.thindie.avezer.feature.home.HomeFlow
import com.thindie.avezer.feature.home.domain.DailyForecast
import com.thindie.avezer.feature.home.domain.HourlyForecastItem
import com.thindie.avezer.feature.home.domain.Weather
import com.thindie.avezer.feature.home.placehourly.placeHourly

internal sealed interface PlaceDetailCommand : Command {
  data object Refresh : PlaceDetailCommand

  data class Init(val weather: Weather) : PlaceDetailCommand

  data object Fetch : PlaceDetailCommand

  data object Back : PlaceDetailCommand

  data class SeeHourlyForecast(val weather: DailyForecast) : PlaceDetailCommand
}

internal suspend fun HomeFlow.exec(
  command: PlaceDetailCommand,
  state: PlaceDetailState,
): PlaceDetailState? =
  when (command) {
    is PlaceDetailCommand.Refresh -> {
      flowModule.repository.fetch()
      null
    }

    is PlaceDetailCommand.Fetch -> {
      flowModule.repository.fetch()
      null
    }

    is PlaceDetailCommand.Back -> {
      back()
      null
    }

    is PlaceDetailCommand.Init -> {
      val hourlyResult =
        command.weather.hourlyForecast.map { hf ->
          HourlyForecastItem(
            time = hf.time,
            temperature = hf.temperature,
            humidity = hf.humidity,
            windSpeed = hf.windSpeed,
            precipitation = hf.precipitation,
            weatherCodeRef = hf.weatherCodeRef,
            emoji = hf.emoji,
          )
        }
      state.copy(
        title = command.weather.city,
        dailyForecast = command.weather.forecast,
        hourlyForecast = hourlyResult,
        weather = command.weather,
      )
    }

    is PlaceDetailCommand.SeeHourlyForecast -> {
      val dailyIndex = state.dailyForecast?.indexOfFirst { it.time == command.weather.time } ?: -1
      val startIndex = state.hourlyForecast.indexOfFirst { it.time.startsWith(command.weather.time) }
      val endIndex =
        if (startIndex >= 0) {
          state.hourlyForecast.indexOfLast { it.time.startsWith(command.weather.time) } + 1
        } else {
          -1
        }
      go(placeHourly(state.weather!!, dailyIndex, startIndex, endIndex))
      null
    }
  }
