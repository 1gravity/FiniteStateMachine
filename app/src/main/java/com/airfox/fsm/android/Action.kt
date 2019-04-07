package com.airfox.fsm.android

import com.airfox.fsm.base.Action

/**
 * These are the actions that trigger state transitions.
 */
object ApplicationStarted : Action()
class DeviceConnected(val connected: Boolean) : Action()
object SplashScreenShown : Action()
class UserAuthenticated(val authenticated: Boolean) : Action()
object Initialized : Action()
object Exit : Action()
