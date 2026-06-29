package com.thindie.avezer.feature.home.search

import com.thindie.avezer.application.storage.StorageId
import com.thindie.avezer.feature.home.HomeFlow
import com.thindie.avezer.feature.home.data.JsonUtil
import com.thindie.avezer.feature.home.domain.Weather
import com.thindie.avezer.feature.home.placehourly.placeHourly

internal suspend fun HomeFlow.exec(
  command: SearchScreenCommand,
  state: SearchScreenState,
): SearchScreenState =
  when (command) {
    is SearchScreenCommand.Search -> {
      state.copy(query = command.query)
    }

    is SearchScreenCommand.ClearSearch -> {
      state.copy(query = "", results = emptyList())
    }

    is SearchScreenCommand.ClearQuery -> {
      state.copy(query = "")
    }

    is SearchScreenCommand.SelectCity -> {
      flowModule.repository.read(command.result.city)
      val storedRaw = flowModule.storage.read(StorageId(command.result.city.lowercase())) ?: return state
      val weather = JsonUtil.fromJson(storedRaw, Weather::class.java) ?: return@exec state
      go(placeHourly(weather))
      state
    }

    is SearchScreenCommand.ToggleFavorite -> {
      flowModule.placesRepository.toggleFavorite(command.city)
      state
    }

    is SearchScreenCommand.OpenSearch -> {
      go(searchPlaces)
      state
    }

    SearchScreenCommand.Back -> {
      back()
      state
    }

    SearchScreenCommand.ConfirmSearch -> {
      flowModule.placesRepository.search(state.query)
      state
    }
  }
