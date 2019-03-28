package com.airfox.fsm.android.state

import com.airfox.fsm.android.Exit
import com.airfox.fsm.base.OneTransitionState

/**
 * Authentication failed.
 */
object AuthenticationFailed : OneTransitionState(Exit, Finish)
