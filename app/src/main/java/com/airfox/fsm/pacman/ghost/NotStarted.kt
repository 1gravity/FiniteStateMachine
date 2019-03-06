package com.airfox.fsm.pacman.ghost

import com.airfox.fsm.base.OneTransitionState
import com.airfox.fsm.pacman.Start

object NotStarted: OneTransitionState(Start, Chase())
