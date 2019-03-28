package com.airfox.fsm.android.state

import com.airfox.fsm.android.InitModel
import com.airfox.fsm.android.Initialized
import com.airfox.fsm.base.Action
import com.airfox.fsm.base.State
import com.airfox.fsm.base.StateImpl

/**
 * Initialize the app.
 */
object InitializeApp : StateImpl() {

    override fun enter(previous: State, action: Action): State {
        return when (InitModel.isInitialized()) {
            true -> exit(Initialized)
            false -> this
        }
    }

    override fun exit(action: Action): State {
        return when (action) {
            is Initialized -> {
                InitModel.setIsInitialized()
                AppReady.enter(this, action)
            }
            else -> super.exit(action)
        }
    }

}
