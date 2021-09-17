package com.airfox.fsm.gol

import io.reactivex.rxjava3.core.Observable

interface Neighbor {
    enum class State {
        ALIVE,
        DEAD
    }

    fun isAlive(): Observable<State>
}
