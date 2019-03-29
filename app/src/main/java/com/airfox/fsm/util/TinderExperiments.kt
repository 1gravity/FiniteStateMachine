package com.airfox.fsm.util

import android.util.Log
import java.lang.IllegalStateException

object TinderExperiments {
    fun execute() {
        val graph2 = TinderStateMachine.createGraph(fsmGraph) {
            state<State.Solid> {
                on<Event.OnMelted> {
                    transitionTo(State.Liquid, SideEffect.LogMelted)
                }
                on<Event.OnMelted> {
                    transitionTo(State.Liquid, SideEffect.LogMelted)
                }
                on<Event.OnMelted> { 
                    dontTransition(SideEffect.LogMelted)
                }
            }
        }
        val graph3 = TinderStateMachine.create(graph2) {
            state<State.Liquid> {
                on<Event.OnFroze> {
                    transitionTo(State.Solid, SideEffect.LogFrozen)
                }
                on<Event.OnVaporized> {
                    transitionTo(State.Gas, SideEffect.LogVaporized)
                }
            }
        }

        graph3.transition(Event.OnMelted)
    }
}

sealed class State {
    object Solid : State()
    object Liquid : State()
    object Gas : State()
}

sealed class Event {
    object OnMelted : Event()
    object OnFroze : Event()
    object OnVaporized : Event()
    object OnCondensed : Event()
}

sealed class SideEffect {
    object LogMelted : SideEffect()
    object LogFrozen : SideEffect()
    object LogVaporized : SideEffect()
    object LogCondensed : SideEffect()
}

val fsmGraph = TinderStateMachine.createGraph<State, Event, SideEffect> {
    initialState(State.Solid)
//    state<State.Solid> {
//        on<Event.OnMelted> {
//            transitionTo(State.Liquid, SideEffect.LogMelted)
//        }
//    }
//    state<State.Liquid> {
//        onEnter {  }
//        on<Event.OnFroze> {
//            transitionTo(State.Solid, SideEffect.LogFrozen)
//        }
//        on<Event.OnVaporized> {
//            transitionTo(State.Gas, SideEffect.LogVaporized)
//        }
//        onExit {  }
//    }
    state<State.Gas> {
        on<Event.OnCondensed> {
            transitionTo(State.Liquid, SideEffect.LogCondensed)
        }
    }
    onTransition { transition ->
        //val validTransition = transition as? TinderStateMachine.Transition.Valid ?: return@onTransition
        when ((transition as? TinderStateMachine.Transition.Valid)?.sideEffect) {
            SideEffect.LogMelted -> Log.w("FiniteStateMachine", "ON_MELTED_MESSAGE")
            SideEffect.LogFrozen -> Log.w("FiniteStateMachine", "ON_FROZEN_MESSAGE")
            SideEffect.LogVaporized -> Log.w("FiniteStateMachine", "ON_VAPORIZED_MESSAGE")
            SideEffect.LogCondensed -> Log.w("FiniteStateMachine", "ON_CONDENSED_MESSAGE")
            else -> throw IllegalStateException("WRONG TRANSITION: ${transition.javaClass.simpleName}")
        }
    }
}