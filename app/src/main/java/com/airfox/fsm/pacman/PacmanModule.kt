package com.airfox.fsm.pacman

import android.annotation.SuppressLint
import androidx.annotation.MainThread
import com.airfox.fsm.ActivityModule
import com.airfox.fsm.base.State
import com.airfox.fsm.pacman.ghost.Ghost
import com.airfox.fsm.pacman.pacman.Collect
import com.airfox.fsm.pacman.pacman.Pacman
import com.airfox.fsm.util.Logger
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import java.util.ArrayList
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.random.Random

class PacmanModule @Inject constructor(private var pacman: Pacman,
                                       private var blinky: Ghost,
                                       private var pinky: Ghost,
                                       private var inky: Ghost,
                                       private var clyde: Ghost,
                                       var logger: Logger
) : ActivityModule() {

    override fun start() {
        startPacman()
        startGhost(blinky)
        startGhost(pinky)
        startGhost(inky)
        startGhost(clyde)
    }

    @SuppressLint("CheckResult")
    private fun startPacman() {
        pacman.transition(Start)
            .doOnSubscribe { addDisposable(it) }
            .subscribeOn(Schedulers.computation())
            .distinctUntilChanged()
            .subscribe { dispatchPacmanState(it) }
    }

    @MainThread
    private fun dispatchPacmanState(state: State) {
        when (state) {
            is Collect -> movePacman()
            is com.airfox.fsm.pacman.pacman.Chase -> movePacman()
            is com.airfox.fsm.pacman.pacman.Dead -> logger.log("Pacman is dead -> GAME OVER")
        }
    }

    private fun movePacman() {
        when (isCollision()) {
            true -> collision()
            false ->
                getNextPosition(pacman.getPosition())?.let { position ->
                    Flowable.just(1).delay(250, TimeUnit.MILLISECONDS).subscribe {
                        pacman.transition(MoveTo(position))
                    }
                }
        }
    }

    @SuppressLint("CheckResult")
    private fun startGhost(stateMachine: Ghost) {
        stateMachine.transition(Start)
            .doOnSubscribe { addDisposable(it) }
            .subscribeOn(Schedulers.computation())
            .distinctUntilChanged()
            .subscribe { dispatchGhostState(stateMachine, it) }
    }

    @MainThread
    private fun dispatchGhostState(ghost: Ghost, state: State) {
        when (state) {
            is com.airfox.fsm.pacman.ghost.Chase -> moveGhost(ghost)
            is com.airfox.fsm.pacman.ghost.Scatter -> moveGhost(ghost)
            is com.airfox.fsm.pacman.ghost.Dead -> logger.log("Ghost is dead")
        }
    }

    private fun moveGhost(ghost: Ghost) {
        when (isCollision()) {
            true -> collision()
            false ->
                getNextPosition(ghost.getPosition())?.let { position ->
                    Flowable.just(1).delay(500, TimeUnit.MILLISECONDS)
                        .subscribe { ghost.transition(MoveTo(position)) }
                }
        }
    }

    private fun isCollision(): Boolean {
        return when (pacman.getPosition()) {
            blinky.getPosition(),
            pinky.getPosition(),
            inky.getPosition(),
            clyde.getPosition() -> true
            else -> false
        }
    }

    private fun collision() {
        pacman.transition(Collision)
        blinky.transition(Collision)
        pinky.transition(Collision)
        inky.transition(Collision)
        clyde.transition(Collision)
    }

    private fun getNextPosition(pos: Position?) : Position? {
        return findPossibleMoves(pos).run {
            val newPos = when (size) {
                0 -> 0
                else -> Random.nextInt(0, size)
            }
            this[newPos]
        }
    }

    private fun findPossibleMoves(pos: Position?): List<Position> {
        return ArrayList<Position>().apply {
            pos?.run {
                if (x < 9) add(Position(x + 1, y))
                if (x > 0) add(Position(x - 1, y))
                if (y < 9) add(Position(x, y + 1))
                if (y > 0) add(Position(x, y - 1))
            }
        }
    }

}
