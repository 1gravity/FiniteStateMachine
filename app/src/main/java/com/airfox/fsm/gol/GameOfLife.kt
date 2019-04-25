package com.airfox.fsm.gol

import android.annotation.SuppressLint
import com.airfox.fsm.ActivityModule
import com.airfox.fsm.util.Logger
import hu.akarnokd.rxjava2.operators.ObservableTransformers
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import java.util.*
import javax.inject.Inject


enum class State {
    ALIVE,
    DEAD
}

class Cell(private var state: State, private val valve: ObservableSource<Boolean>) {

    private val neighborStates = BitSet()

    private val emitter = BehaviorSubject.create<State>().apply { onNext(state) }

    fun setNeighbors(neighbors: ArrayList<Cell>) {
        neighbors.forEachIndexed { index, cell ->
            cell.states()
                .compose(ObservableTransformers.valve<State>(valve))
                .subscribe { neighborStates.set(index, it == State.ALIVE ) }
        }
    }

    fun state() = state

    fun states(): Observable<State> = emitter

    fun step() {
        state = when (neighborStates.cardinality()) {
            2 -> state
            3 -> State.ALIVE
            else -> State.DEAD
        }
        emitter.onNext(state)
    }

}

class GameOfLife(private val initialCells: Array<IntArray>, private val logger: Logger) {

    // we use this to freeze the state of cells till a step has been calculated
    private val valve = PublishSubject.create<Boolean>()

    // create the initial grid of Cells
    private val cells = Array(initialCells.size) { x ->
        Array(initialCells[x].size) { y ->
            val state = if (initialCells[x][y] == 1) State.ALIVE else State.DEAD
            Cell(state, valve)
        }
    }

    // precomputed (x, y) offsets for neighbors
    private val offsets = arrayListOf<Pair<Int, Int>>().apply {
        add(Pair(-1, -1))
        add(Pair(0, -1))
        add(Pair(+1, -1))
        add(Pair(-1, 0))
        add(Pair(1, 0))
        add(Pair(-1, 1))
        add(Pair(0, 1))
        add(Pair(1, 1))
    }

    init {
        // calculate the neighbors
        cells.forEachIndexed { x, arrayOfCells ->
            arrayOfCells.forEachIndexed { y, cell ->
                val neighbors = offsets.fold(arrayListOf<Cell>()) { neighbors, offset ->
                    checkAndAddNeighbor(x, y, offset, neighbors)
                }
                cell.setNeighbors(neighbors)
            }
        }
    }

    private fun checkAndAddNeighbor(x: Int, y: Int, offset: Pair<Int, Int>, neighbors: ArrayList<Cell>): ArrayList<Cell> {
        val nX = x + offset.first
        val nY = y + offset.second
        if (nX >= 0 && nY >= 0 && nX < cells.size && nY < cells[nX].size) {
            neighbors.add(cells[nX][nY])
        }
        return neighbors
    }

    fun step() {
        valve.onNext(false)
        cells.forEach { it.forEach { it.step() } }
        valve.onNext(true)
    }

    fun print() {
        logger.log("---------------------------------------")
        cells.forEach {
            it.fold(StringBuilder()) { s, cell ->
                val value = if (cell.state() == State.ALIVE) "1" else "0"
                s.append(value)
            }.run { logger.log(toString()) }
        }
    }

}

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