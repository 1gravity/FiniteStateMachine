package com.airfox.fsm.android.state

import com.airfox.fsm.android.InitModel
import com.airfox.fsm.android.UserAuthenticated
import com.airfox.fsm.base.Action
import com.airfox.fsm.base.State
import com.airfox.fsm.base.StateImpl

/**
 * Authenticate the user.
 */
object AuthenticateUser : StateImpl() {

    override fun enter(previous: State, action: Action): State {
        return when (InitModel.isAuthenticated()) {
            true -> exit(UserAuthenticated(true))
            false -> this
        }
    }

    override fun exit(action: Action): State {
        return when (action) {
            is UserAuthenticated ->
                return when (action.authenticated) {
                    true -> InitializeApp.enter(this, action)
                    false -> AuthenticationFailed.enter(this, action)
                }
            else -> super.exit(action)
        }
    }

}
