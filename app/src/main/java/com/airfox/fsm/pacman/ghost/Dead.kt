package com.airfox.fsm.pacman.ghost

import com.airfox.fsm.base.OneTransitionState

object Dead: OneTransitionState(Actions.Start, Chase())
