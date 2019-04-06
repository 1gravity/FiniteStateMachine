package com.airfox.fsm.pacman.pacman

import com.airfox.fsm.base.Action
import com.airfox.fsm.base.State
import com.airfox.fsm.base.StateImpl
import com.airfox.fsm.pacman.Collision
import com.airfox.fsm.pacman.MoveTo
import com.airfox.fsm.pacman.Position
import com.airfox.fsm.pacman.PowerPillEnds

class Chase(val pos: Position): StateImpl() {

    override fun exit(action: Action): State {
        return when (action) {
            is MoveTo -> Chase(action.pos).enter(this, action)
            is PowerPillEnds -> Collect(pos).enter(this, action)
            is Collision -> Chase(pos).enter(this, action)
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
