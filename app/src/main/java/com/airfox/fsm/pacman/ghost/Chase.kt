package com.airfox.fsm.pacman.ghost

import com.airfox.fsm.base.Action
import com.airfox.fsm.base.State
import com.airfox.fsm.pacman.Collision
import com.airfox.fsm.pacman.MoveTo
import com.airfox.fsm.pacman.PacmanEatsPill

class Chase(val position: Pair<Int, Int>): State {

    constructor(): this(Pair(0,0))

    override fun enter(previous: State, action: Action): State {
        return when (action) {
            is MoveTo, PacmanEatsPill, Collision -> exit(action)
            else -> this
        }
    }

    override fun exit(action: Action): State {
        return when (action) {
            is MoveTo -> Chase(action.pos)
            is PacmanEatsPill -> Scatter(position).enter(this, action)
            is Collision -> Celebrate.enter(this, action)
            else -> this
        }
    }

    override fun equals(other: Any?): Boolean {
        return when {
            other == null -> false
            other !is Chase -> false
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
