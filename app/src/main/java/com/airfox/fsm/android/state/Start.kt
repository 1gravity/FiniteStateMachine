package com.airfox.fsm.android.state

import com.airfox.fsm.android.ApplicationStarted
import com.airfox.fsm.base.OneTransitionState

/**
 * Initial state.
 */
object Start : OneTransitionState(ApplicationStarted, CheckConnection)
