package com.airfox.fsm

import android.annotation.SuppressLint
import androidx.annotation.MainThread
import com.airfox.fsm.base.State
import com.airfox.fsm.pacman.Collision
import com.airfox.fsm.pacman.MoveTo
import com.airfox.fsm.pacman.Position
import com.airfox.fsm.pacman.Start
import com.airfox.fsm.pacman.ghost.Chase
import com.airfox.fsm.pacman.ghost.Dead
import com.airfox.fsm.pacman.ghost.Ghost
import com.airfox.fsm.pacman.ghost.Scatter
import com.airfox.fsm.pacman.pacman.Collect
import com.airfox.fsm.pacman.pacman.Pacman
import com.airfox.fsm.util.Logger
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.ArrayList
import javax.inject.Inject

class PacmanModule @Inject constructor(private var pacman: Pacman,
                                       private var blinky: Ghost,
                                       private var pinky: Ghost,
                                       private var inky: Ghost,
                                       private var clyde: Ghost,
                                       var logger: Logger
) : ActivityModule() {

    fun startGame() {
        startPacman(pacman)
        startGhost(blinky)
        startGhost(pinky)
        startGhost(inky)
        startGhost(clyde)
    }

    @SuppressLint("CheckResult")
    private fun <T : Pacman> startPacman(stateMachine: T) {
        stateMachine.transition(Start)
            .doOnSubscribe { addDisposable(it) }
            .distinctUntilChanged()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { dispatchPacmanState(stateMachine, it) }
    }

    @MainThread
    private fun dispatchPacmanState(pacman: Pacman, state: State) {
        logger.log("dispatching state: ${state.javaClass.simpleName}")

        when (state) {
            is Collect -> {
                state.pos
            }
            is Chase -> {

            }
            is Dead -> logger.log("Pacman is dead -> Game over}")
        }
    }

    @SuppressLint("CheckResult")
    private fun <T : Ghost> startGhost(stateMachine: T) {
        stateMachine.transition(Start)
            .doOnSubscribe { addDisposable(it) }
            .distinctUntilChanged()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { dispatchGhostState(stateMachine, it) }
    }

    @MainThread
    private fun dispatchGhostState(ghost: Ghost, state: State) {
        logger.log("dispatching state: ${state.javaClass.simpleName}")

        when (state) {
            is Chase -> {
                // try to get closer to the Pacman
                val newPos = with(state.pos) { Position(x, y + 1) }
                when (newPos.y) {
                    in 0..10 -> ghost.transition(MoveTo(newPos))
                    else -> ghost.transition(Collision)
                }
            }
            is Scatter -> {
                // try to get away from the Pacman
                val newPos = state.pos
                ghost.transition(MoveTo(newPos))
            }
            is Dead -> logger.log("Ghost is dead")
        }
    }

    private fun findPossibleMoves(pos: Position): List<Position> {
        return ArrayList<Position>().apply {
            when {
                pos.x < 9 -> add(Position(pos.x + 1, pos.y))
                pos.x > 1 -> add(Position(pos.x - 1, pos.y))
                pos.y < 9 -> add(Position(pos.x, pos.y + 1))
                pos.y > 1 -> add(Position(pos.x, pos.y - 1))
            }
        }
    }

}