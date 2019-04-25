package com.airfox.fsm.gol

import android.annotation.SuppressLint
import hu.akarnokd.rxjava2.operators.ObservableTransformers
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import java.util.*

@SuppressLint("CheckResult")
class Cell(private var state: State, private val valve: Valve) : Neighbor {

    enum class State {
        ALIVE,
        DEAD
    }

    private val emitter = BehaviorSubject.create<State>().apply { onNext(state) }

    private val neighborsAreAlive = BitSet()

    override fun isAlive(): Observable<Boolean> = emitter.map { it == State.ALIVE }

    fun isAliveNow() = state == State.ALIVE

    fun setNeighbors(neighbors: ArrayList<Neighbor>) {
        neighbors.forEachIndexed { index, neighbor -> subscribe(index, neighbor.isAlive()) }
    }

    private fun subscribe(index: Int, isAlive: Observable<Boolean>) {
        isAlive
            .compose(ObservableTransformers.valve<Boolean>(valve.isOpen()))
            .subscribe { neighborsAreAlive.set(index, it ) }
    }

    fun step() {
        state = when (neighborsAreAlive.cardinality()) {
            2 -> state
            3 -> State.ALIVE
            else -> State.DEAD
        }
        emitter.onNext(state)
    }

}
