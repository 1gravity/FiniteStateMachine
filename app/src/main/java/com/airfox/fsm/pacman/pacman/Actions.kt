package com.airfox.fsm.pacman.pacman

import com.airfox.fsm.base.Action

/**
 * These are the actions that trigger state transitions.
 */
sealed class Actions {
    object Start : Action
    class MoveTo(val pos: Pair<Int, Int>) : Action
    object PacmanEatsPill : Action
    object PowerPillEnds : Action
    object CollisionWithPacman : Action
}
