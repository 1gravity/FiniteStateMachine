package com.airfox.fsm.android.state

import com.airfox.fsm.android.Exit
import com.airfox.fsm.base.OneTransitionState

/**
 * Device is offline.
 */
object DeviceOffline : OneTransitionState(Exit, Finish)
