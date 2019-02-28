package com.airfox.fsm.base

/**
 * All states inherit from this interface.
 *
 * Some rules:
 * - enter checks whether the Action that lead to this State. If the entry Action isn't recognized, we have a
 * - enter should be used to check pre-conditions only.
 *   If the state accepts the transition it has to return `this`,
 *   if the state determines the next state should be processed it should call
 *   `exit(action)` with the action being processed by the exit function.
 *   That way all the logic related to exiting is state is encapsulated in the exit function.
 * - exit should return this if the state wants to stay in that state and call
 *   otherState.enter(action) to transition to another state)
 */
interface State {

    /**
     * Before a State transition happens, the State to transition to can check pre-conditions
     * and return an alternative State instead.
     *
     * @param previous The previous State that tries to transition to this one.
     * @param action The Action that led the previous State to transition to this one.
     *
     * @return `this` if the transition is accepted or an alternative State if the transition is
     *         rejected (or forwarded).
     */
    fun enter(previous: State, action: Action) : State

    /**
     * An action is performed on this State resulting in a State transition.
     *
     * @param action The Action that was triggered.
     *
     * @return Whe new state the state machine should transition to based on the action parameter, could also be this
     *         if we don't want to change state.
     */
    fun exit(action: Action): State

}
