package com.airfox.fsm.gol

import com.airfox.fsm.util.Logger

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

    init {
        // calculate the neighbors
        cells.forEachIndexed { x, arrayOfCells ->
            arrayOfCells.forEachIndexed { y, cell -> addNeighbors(cell, x, y) }
        }
    }

    private fun addNeighbors(cell: Cell, x: Int, y: Int) {
        val neighbors = arrayListOf<Neighbor>()
        for (nX in x-1..x+1) {
            if (nX !in 0 until cells.size) continue
            for (nY in y-1..y+1) {
                if (nY !in 0 until cells[nX].size) continue
                if (nX != x || nY != y) neighbors.add(cells[nX][nY])
            }
        }
        cell.setNeighbors(neighbors)
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
