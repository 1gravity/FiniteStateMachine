package com.airfox.fsm.android.state

import com.airfox.fsm.android.InitModel
import com.airfox.fsm.android.SplashScreenShown
import com.airfox.fsm.base.Action
import com.airfox.fsm.base.State
import com.airfox.fsm.base.StateImpl

/**
 * Show the splash screen.
 */
object ShowSplashScreen : StateImpl() {

    override fun enter(previous: State, action: Action): State {
        return when (InitModel.isSplashScreenShown()) {
            true -> exit(SplashScreenShown(true))
            false -> this
        }
    }

    override fun exit(action: Action): State {
        return when (action) {
            is SplashScreenShown -> {
                InitModel.setSplashScreenShown()
                AuthenticateUser.enter(this, action)
            }
            else -> super.exit(action)
        }
    }

}
