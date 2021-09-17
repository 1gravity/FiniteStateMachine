package com.airfox.fsm.base

import android.annotation.SuppressLint
import com.airfox.fsm.util.Logger
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import javax.inject.Singleton
import kotlin.properties.Delegates

@Singleton
@SuppressLint("CheckResult")
abstract class StateMachineImpl(private val logger: Logger, startState: State) : StateMachine {

    private val states = BehaviorSubject.create<State>()

    private var theState by Delegates.observable(startState) { _, oldState, newState ->
        if (oldState != newState) {
            logger.log("${javaClass.simpleName} state:  $oldState -> $newState")
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
        logger.log("${javaClass.simpleName} action: $action")

        theState = theState.exit(action)

        return states
    }

}
