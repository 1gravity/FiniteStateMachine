package com.airfox.fsm.base

import android.annotation.SuppressLint
import android.util.Log
import com.airfox.fsm.logTag
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Singleton
import kotlin.properties.Delegates

@Singleton
@SuppressLint("CheckResult")
abstract class StateMachineImpl constructor(startState: State) : StateMachine {

    private val states = BehaviorSubject.create<State>()

    private var theState by Delegates.observable(startState) { _, oldState, newState ->
        if (oldState != newState) {
            Log.i(logTag, "change state: $oldState -> $newState")
            states.onNext(newState)
        }
    }

    override fun getState() = theState

    override fun getStates() = states

    /**
     * Transition from the current State to another State based on Action.
     * Current State will decide if Action is applicable and return the new State (or itself if no
     * state transition is needed)
     */
    @Synchronized override fun transition(action: Action): Observable<State> {
        theState = theState.exit(action)

        return states
    }

}
