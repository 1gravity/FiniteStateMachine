package com.airfox.fsm.android.state

import com.airfox.fsm.android.ApplicationStarted
import com.airfox.fsm.base.OneTransitionState

/**
 * Exit the Activity.
 */
object Finish : OneTransitionState(ApplicationStarted, Start)
