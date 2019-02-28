package com.airfox.fsm.base

/**
 * Some states have a single transition.
 *
 * @param exitAction do the transition to nextState if the action is exitAction.
 * @param nextState the State to transition to if exitAction happens.
 */
abstract class OneTransitionState(private val exitAction: Action, private val nextState: State) : StateImpl() {

    override fun exit(action: Action): State {
        return when (action) {
            exitAction -> nextState.enter(this, action)
            else -> super.exit(action)
        }
    }

}
