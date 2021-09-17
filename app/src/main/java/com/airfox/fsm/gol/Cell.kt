package com.airfox.fsm.gol

import com.jakewharton.rxrelay3.BehaviorRelay
import hu.akarnokd.rxjava3.operators.ObservableTransformers
import io.reactivex.rxjava3.core.Observable
import java.util.*
import com.airfox.fsm.gol.Neighbor.State
import kotlin.properties.Delegates

class Cell(private var initialState: State, private val valve: Valve) : Neighbor {

    private val emitter = BehaviorRelay.create<State>().apply { accept(initialState) }

    override fun isAlive(): Observable<State> = emitter.map { it }

    private val neighborStates = BitSet()

    private var state by Delegates.observable(initialState) { _, oldState, newState ->
        if (oldState != newState) {
            emitter.accept(newState)
        }
    }

    fun setNeighbors(neighbors: Collection<Neighbor>) {
        neighbors.forEachIndexed { index, neighbor ->
            neighbor.isAlive()
                .compose(ObservableTransformers.valve(valve.isOpen()))
                .subscribe { state ->
                    neighborStates.set(index, state == State.ALIVE)
                }
        }
    }

    fun isAliveNow() = state == State.ALIVE

    fun step() {
        state = when (neighborStates.cardinality()) {
            2 -> state
            3 -> State.ALIVE
            else -> State.DEAD
        }
    }

}
