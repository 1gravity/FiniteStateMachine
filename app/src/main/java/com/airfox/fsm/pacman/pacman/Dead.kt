package com.airfox.fsm.pacman.pacman

import com.airfox.fsm.base.OneTransitionState
import com.airfox.fsm.pacman.Start

object Dead: OneTransitionState(Start, Collect())
