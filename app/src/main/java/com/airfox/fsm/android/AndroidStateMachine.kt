package com.airfox.fsm.android

import com.airfox.fsm.android.state.Start
import com.airfox.fsm.base.StateMachineImpl
import com.airfox.fsm.util.Logger
import javax.inject.Inject

class AndroidStateMachine @Inject constructor(logger: Logger): StateMachineImpl(logger, Start)
