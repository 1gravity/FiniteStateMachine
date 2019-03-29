package com.airfox.fsm.door

import com.airfox.fsm.base.Action
import com.airfox.fsm.base.State
import com.airfox.fsm.base.StateImpl
import com.airfox.fsm.base.StateMachineImpl
import com.airfox.fsm.util.Logger
import javax.inject.Inject

object Open : Action()

object Close : Action()

object Opened : StateImpl() {
    override fun exit(action: Action): State = if (action is Close) Closed.enter(this, action) else this
}

object Closed : StateImpl() {
    override fun exit(action: Action): State = if (action is Open) Opened.enter(this, action) else this
}

class Door @Inject constructor(logger: Logger, initialState: State) : StateMachineImpl(logger, initialState)

/**
 * This is a more rigid implementation of the Closed state.
 */
//object ClosedSophisticated : StateImpl() {
//
//    override fun enter(previous: State, action: Action): State {
//        return when(action) {
//            is Open -> this
//            else -> throw IllegalStateException()
//        }
//    }
//
//    override fun exit(action: Action): State {
//        return when(action) {
//            is Open -> Opened.enter(this, action)
//            else -> throw IllegalStateException()
//        }
//    }
//
//}
