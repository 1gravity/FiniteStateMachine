package com.airfox.fsm.pacman.ghost

import com.airfox.fsm.base.OneTransitionState

object GameStart: OneTransitionState(Actions.Start, Chase())
