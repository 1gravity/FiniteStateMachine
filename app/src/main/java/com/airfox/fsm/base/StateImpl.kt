package com.airfox.fsm.base

abstract class StateImpl: State {

    /**
     * The default implementation accepts any Action as entry condition.
     */
    override fun enter(previous: State, action: Action) : State {
        return this
    }

    /**
     * The default implementation does nothing but always stays in the current state.
     */
    override fun exit(action: Action): State {
        return this
    }

}

