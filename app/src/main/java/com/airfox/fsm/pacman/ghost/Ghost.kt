package com.airfox.fsm.pacman.ghost

import com.airfox.fsm.base.StateMachineImpl
import com.airfox.fsm.pacman.Position
import com.airfox.fsm.util.Logger
import javax.inject.Inject

class Ghost @Inject constructor(logger: Logger): StateMachineImpl(logger, NotStarted) {

    fun getPosition() : Position? {
        val state = getState()
        return when(state) {
            is Chase -> state.pos
            is Scatter -> state.pos
            else -> null
        }
    }

}