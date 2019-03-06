package com.airfox.fsm.pacman

import com.airfox.fsm.base.Action

// Start the game.
object Start : Action

// Move to a new position.
class MoveTo(val pos: Pair<Int, Int>) : Action

// Pac-Man has eaten a power pill.
object PacmanEatsPill : Action

// The effect of the power pill has ended.
object PowerPillEnds : Action

// A collision has happened.
object Collision : Action
