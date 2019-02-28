package com.airfox.fsm

import com.airfox.fsm.base.Action
import com.airfox.fsm.base.State
import com.airfox.fsm.base.StateImpl
import com.airfox.fsm.base.StateMachineImpl
import java.lang.IllegalStateException

object Open : Action

object Close : Action

object Opened : StateImpl() {
    override fun exit(action: Action): State {
        // this is a relaxed implementation of the Opened state
        return if (action is Close) Closed.enter(this, action) else this

        /* this is a more rigid implementation of the Opened state
        return when(action) {
            is Close -> Closed.enter(this, action)
            is Open -> this
            else -> throw IllegalStateException()
        } */
    }
}

object Closed : StateImpl() {
    override fun exit(action: Action): State {
        // this is a relaxed implementation of the Closed state
        return if (action is Open) Opened.enter(this, action) else this

        /* this is a more rigid implementation of the Closed state
        return when(action) {
            is Open -> Opened.enter(this, action)
            is Close -> this
            else -> throw IllegalStateException()
        } */
    }
}

class Door(initialState: State) : StateMachineImpl(initialState)
