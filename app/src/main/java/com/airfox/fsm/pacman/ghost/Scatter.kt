package com.airfox.fsm.pacman.ghost

import android.util.Log
import com.airfox.fsm.base.Action
import com.airfox.fsm.base.State
import com.airfox.fsm.logTag

class Scatter(val position: Pair<Int, Int>): State {

    constructor(): this(Pair(0,0))

    override fun enter(previous: State, action: Action): State {
        return when (action) {
            is Actions.MoveTo -> {
                Log.i(logTag, "Ghost moved to ${action.pos.first}/${action.pos.second}")
                Scatter(action.pos)
            }
            is Actions.PowerPillEnds, Actions.CollisionWithPacman -> exit(action)
            else -> this
        }
    }

    override fun exit(action: Action): State {
        return when (action) {
            is Actions.Start -> Chase(Pair(0, 0)).enter(this, action)
            is Actions.MoveTo -> {
                Log.i(logTag, "Ghost moved to ${action.pos.first}/${action.pos.second}")
                Scatter(action.pos)
            }
            is Actions.PacmanEatsPill -> enter(this, action)
            is Actions.CollisionWithPacman -> Dead.enter(this, action)
            else -> this
        }
    }

    override fun equals(other: Any?): Boolean {
        return when {
            other == null -> false
            other !is Scatter -> false
            other.position != position -> false
            else -> true
        }
    }

    override fun hashCode(): Int {
        return position.hashCode()
    }


    override fun toString(): String {
        return "${javaClass.simpleName}: ${position.first}/${position.second}"
    }

}

