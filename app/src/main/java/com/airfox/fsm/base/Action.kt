package com.airfox.fsm.base

/**
 * An Action triggers a State transition.
 * We could implement this as simple marker interface (like Clonable or Serializable)
 * but we want to override the toString() method.
 */
open class Action {
    override fun toString(): String {
        return javaClass.simpleName
    }
}
