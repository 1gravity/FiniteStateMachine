package com.airfox.fsm

import android.annotation.SuppressLint
import androidx.annotation.MainThread
import com.airfox.fsm.base.State
import com.airfox.fsm.pacman.Collision
import com.airfox.fsm.pacman.MoveTo
import com.airfox.fsm.pacman.Start
import com.airfox.fsm.pacman.ghost.Chase
import com.airfox.fsm.pacman.ghost.Dead
import com.airfox.fsm.pacman.ghost.Ghost
import com.airfox.fsm.pacman.ghost.Scatter
import com.airfox.fsm.pacman.pacman.Pacman
import com.airfox.fsm.util.Logger
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class PacmanModule @Inject constructor(private var pacman: Pacman,
                                       private var blinky: Ghost,
                                       private var pinky: Ghost,
                                       private var inky: Ghost,
                                       private var clyde: Ghost,
                                       var logger: Logger
) : ActivityModule() {

    fun startGame() {
        pacman.transition(Start)
        blinky.transition(Start)
        pinky.transition(Start)
        inky.transition(Start)
        clyde.transition(Start)
        observeGhost(blinky)
        observeGhost(pinky)
        observeGhost(inky)
        observeGhost(clyde)
    }

    @SuppressLint("CheckResult")
    private fun observeGhost(ghost: Ghost) {
        ghost.getStates()
            .doOnSubscribe { addDisposable(it) }
            .distinctUntilChanged()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { dispatchState(ghost, it) }
    }

    @MainThread
    private fun dispatchState(ghost: Ghost, state: State) {
        logger.log("dispatching state: ${state.javaClass.simpleName}")

        when (state) {
            is Chase -> {
                // try to get closer to the Pacman
                val newPos = with(state.pos) { Pair(first, second + 1) }
                when (newPos.second) {
                    in 0..10 -> ghost.transition(MoveTo(newPos))
                    else -> ghost.transition(Collision)
                }
            }
            is Scatter -> {
                // try to get away from the Pacman
                val newPos = state.pos
                ghost.transition(MoveTo(newPos))
            }
            is Dead -> {
                // ghost is dead -> show some "deadly" animation and remove it from the game
            }
        }
    }

}