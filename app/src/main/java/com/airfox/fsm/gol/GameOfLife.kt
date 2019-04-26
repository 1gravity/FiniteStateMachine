package com.airfox.fsm.gol

import com.airfox.fsm.util.Logger
import java.util.ArrayList

class GameOfLife(private val initialCells: Array<IntArray>, private val logger: Logger) {

    // we use this to freeze the propagation of state for cells for each step
    private val valve = Valve()

    // create the initial grid of Cells
    private val cells = Array(initialCells.size) { x ->
        Array(initialCells[x].size) { y ->
            val state = if (initialCells[x][y] == 1) Cell.State.ALIVE else Cell.State.DEAD
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
                val neighbors = offsets.fold(arrayListOf<Neighbor>()) { neighbors, offset ->
                    checkAndAddNeighbor(x, y, offset, neighbors)
                }
                cell.setNeighbors(neighbors)
            }
        }
    }

    private fun checkAndAddNeighbor(x: Int, y: Int, offset: Pair<Int, Int>, neighbors: ArrayList<Neighbor>): ArrayList<Neighbor> {
        val nX = x + offset.first
        val nY = y + offset.second
        if (nX >= 0 && nY >= 0 && nX < cells.size && nY < cells[nX].size) {
            neighbors.add(cells[nX][nY])
        }
        return neighbors
    }

    fun step() {
        valve.close()
        cells.forEach { it.forEach { it.step() } }
        valve.open()
    }

    fun print() {
        logger.log("---------------------------------------")
        cells.forEach {
            it.fold(StringBuilder()) { s, cell ->
                val value = if (cell.isAliveNow()) "1" else "0"
                s.append(value)
            }.run { logger.log(toString()) }
        }
    }

}

