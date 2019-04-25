package com.airfox.fsm.gol

import io.reactivex.Observable

interface Neighbor {
    fun isAlive(): Observable<Boolean>
}
