package com.airfox.fsm.pacman.pacman

import com.airfox.fsm.base.Action
import com.airfox.fsm.base.State
import com.airfox.fsm.base.StateImpl
import com.airfox.fsm.pacman.Collision
import com.airfox.fsm.pacman.MoveTo
import com.airfox.fsm.pacman.PacmanEatsPill

class Collect(val pos: Pair<Int, Int>): StateImpl() {

    constructor(): this(Pair(0,0))

    override fun enter(previous: State, action: Action): State {
        return when (action) {
            is MoveTo, PacmanEatsPill, Collision -> exit(action)
            else -> this
        }
    }

    override fun exit(action: Action): State {
        return when (action) {
            is MoveTo -> Collect(action.pos)
            is PacmanEatsPill -> Chase(pos).enter(this, action)
            is Collision -> Dead.enter(this, action)
            else -> this
        }
    }

    override fun equals(other: Any?): Boolean {
        return when {
            other == null -> false
            other !is Collect -> false
            other.pos != pos -> false
            else -> true
        }
    }

    override fun hashCode(): Int {
        return pos.hashCode()
    }

    override fun toString(): String {
        return "${javaClass.simpleName}: ${pos.first}/${pos.second}"
    }

}
