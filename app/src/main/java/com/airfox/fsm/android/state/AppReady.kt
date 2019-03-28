package com.airfox.fsm.android.state

import com.airfox.fsm.android.ApplicationStarted
import com.airfox.fsm.base.OneTransitionState

/**
 * Initialization done. App is good to go.
 */
object AppReady : OneTransitionState(ApplicationStarted, Start)