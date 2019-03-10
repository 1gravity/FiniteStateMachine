package com.airfox.fsm.pacman

import com.airfox.fsm.base.Action

// Start the game.
object Start : Action

// Move to a new pos.
class MoveTo(val pos: Pair<Int, Int>) : Action {
    override fun toString(): String {
        return "${javaClass.simpleName}: ${pos.first}/${pos.second}"
    }
}

// Pac-Man has eaten a power pill.
object PacmanEatsPill : Action

// The effect of the power pill has ended.
object PowerPillEnds : Action

// A collision has happened.
object Collision : Action
