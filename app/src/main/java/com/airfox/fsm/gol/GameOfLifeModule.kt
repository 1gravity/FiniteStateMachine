package com.airfox.fsm.gol

import android.annotation.SuppressLint
import com.airfox.fsm.ActivityModule
import com.airfox.fsm.util.Logger
import javax.inject.Inject

class GameOfLifeModule @Inject constructor(private val logger: Logger) : ActivityModule() {

    @SuppressLint("CheckResult")
    override fun start() {

        val initialCells: Array<IntArray> = arrayOf(
            intArrayOf(0, 0, 0, 0, 0, 0),
            intArrayOf(0, 1, 1, 0, 0, 0),
            intArrayOf(0, 1, 1, 0, 0, 0),
            intArrayOf(0, 0, 0, 1, 1, 0),
            intArrayOf(0, 0, 0, 1, 1, 0),
            intArrayOf(0, 0, 0, 0, 0, 0)
        )

        val game = GameOfLife(initialCells, logger)
        game.print()

        for (i in 1..10) {
            game.step()
            game.print()
        }
    }

}