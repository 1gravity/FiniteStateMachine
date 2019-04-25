package com.airfox.fsm.gol

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class Valve {

    enum class State {
        OPEN,
        CLOSED
    }

    private val valve = PublishSubject.create<State>()

    fun isOpen(): Observable<Boolean> = valve.map { it == State.OPEN }

    fun open() {
        valve.onNext(State.OPEN)
    }

    fun close() {
        valve.onNext(State.CLOSED)
    }

}
