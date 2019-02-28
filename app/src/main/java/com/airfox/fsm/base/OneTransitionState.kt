package com.airfox.fsm.base

/**
 * Some states have a single transition.
 *
 * @param exitAction do the transition to nextState if the action is exitAction, if exitAction
 *                   is Null then all actions are accepted to trigger the transition
 */
abstract class OneTransitionState(private val exitAction: Action?,
                                  private val nextState: State
) : StateImpl() {

    override fun exit(action: Action): State {
        return when {
            action == exitAction || exitAction == null -> nextState.enter(this, action)
            else -> super.exit(action)
        }
    }

    override fun toString(): String {
        return javaClass.simpleName
    }

}
