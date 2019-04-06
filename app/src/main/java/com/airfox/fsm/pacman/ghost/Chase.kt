package com.airfox.fsm.pacman.ghost

import com.airfox.fsm.base.Action
import com.airfox.fsm.base.State
import com.airfox.fsm.base.StateImpl
import com.airfox.fsm.pacman.Collision
import com.airfox.fsm.pacman.MoveTo
import com.airfox.fsm.pacman.PacmanEatsPill
import com.airfox.fsm.pacman.Position

class Chase(val pos: Position) : StateImpl() {

    // the start position of a Ghost is [0, 0]
    constructor(): this(Position(0,0))

    override fun exit(action: Action): State {
        return when (action) {
            is MoveTo -> Chase(action.pos).enter(this, action)
            is PacmanEatsPill -> Scatter(pos).enter(this, action)
            is Collision -> Celebrate.enter(this, action)
            else -> this
        }
    }

    override fun equals(other: Any?): Boolean {
        return when {
            other == null -> false
            other !is Chase -> false
            other.pos != pos -> false
            else -> true
        }
    }

    override fun hashCode(): Int {
        return pos.hashCode()
    }


    override fun toString(): String {
        return "${javaClass.simpleName}: ${pos.x}/${pos.y}"
    }

}
