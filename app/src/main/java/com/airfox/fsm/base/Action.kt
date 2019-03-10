package com.airfox.fsm.base

/**
 * An Action triggers a State transition.
 * We could implement it as simple marker interface but we want to override the toString method.
 */
open class Action {
    override fun toString(): String {
        return javaClass.simpleName
    }
}
