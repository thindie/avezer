package com.thindie.avezer.feature.home.places

import com.thindie.avezer.engine.Command
import com.thindie.avezer.feature.home.domain.MainRepository

internal sealed interface ScreenCommand : Command {
    data object Fetch : ScreenCommand

    data object Back : ScreenCommand
}


internal suspend fun exec(
    command: ScreenCommand,
    state: ScreenState,
    repository: MainRepository,
): ScreenState = when (command) {
    is ScreenCommand.Fetch -> {
        repository.fetch()
        state
    }

    is ScreenCommand.Back -> {
        state
    }
}
