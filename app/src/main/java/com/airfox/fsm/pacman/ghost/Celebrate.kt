package com.airfox.fsm.pacman.ghost

import com.airfox.fsm.base.OneTransitionState
import com.airfox.fsm.pacman.Start

object Celebrate: OneTransitionState(Start, Chase())
