package com.airfox.fsm.pacman.pacman

import com.airfox.fsm.base.StateMachineImpl
import com.airfox.fsm.util.Logger
import javax.inject.Inject

class Pacman @Inject constructor(logger: Logger): StateMachineImpl(logger, NotStarted)
