package com.airfox.fsm.pacman.ghost

import com.airfox.fsm.base.Action
import com.airfox.fsm.base.State
import com.airfox.fsm.base.StateImpl
import com.airfox.fsm.pacman.Collision
import com.airfox.fsm.pacman.MoveTo
import com.airfox.fsm.pacman.PowerPillEnds

class Scatter(val pos: Pair<Int, Int>): StateImpl() {

    override fun enter(previous: State, action: Action): State {
        return when (action) {
            is MoveTo, PowerPillEnds, Collision -> exit(action)
            else -> this
        }
    }

    override fun exit(action: Action): State {
        return when (action) {
            is MoveTo -> Scatter(action.pos)
            is PowerPillEnds -> Chase(pos).enter(this, action)
            is Collision -> Dead.enter(this, action)
            else -> this
        }
    }

    override fun equals(other: Any?): Boolean {
        return when {
            other == null -> false
            other !is Scatter -> false
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

