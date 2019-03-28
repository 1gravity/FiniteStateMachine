package com.airfox.fsm.android.state

import com.airfox.fsm.android.DeviceConnected
import com.airfox.fsm.base.Action
import com.airfox.fsm.base.State
import com.airfox.fsm.base.StateImpl
import com.airfox.fsm.util.NetworkState

/**
 * Check the network connection.
 */
object CheckConnection : StateImpl() {

    override fun enter(previous: State, action: Action): State {
        return when (NetworkState.isOnline()) {
            true -> exit(DeviceConnected(true))
            false -> this
        }
    }

    override fun exit(action: Action): State {
        return when (action) {
            is DeviceConnected ->
                when (action.connected) {
                    true -> ShowSplashScreen.enter(this, action)
                    false -> DeviceOffline.enter(this, action)
                }
            else -> super.exit(action)
        }
    }

}
