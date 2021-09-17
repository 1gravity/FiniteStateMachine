package com.airfox.fsm.base

import io.reactivex.rxjava3.core.Observable

interface StateMachine {

    /**
     * @return the current state.
     */
    fun getState(): State

    /**
     * @return the states as an RX Observable.
     */
    fun getStates(): Observable<State>

    /**
     * Transition from the current State to another State based on Action.
     * The current State will decide if Action is applicable and return the new State (or itself if no
     * state transition is needed).
     *
     * @param action The Action that was triggered.
     *
     * @return the states as an RX Observable.
     */
    fun transition(action: Action): Observable<State>

}
