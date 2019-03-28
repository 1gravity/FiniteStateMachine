package com.airfox.fsm.util

import android.content.Context
import android.content.SharedPreferences

object PreferenceUtil {

    private val prefs: SharedPreferences by lazy {
        theContext.getSharedPreferences("FiniteStateMachine", Context.MODE_PRIVATE)
    }

    fun getBoolean(key: String, defValue: Boolean): Boolean {
        return prefs.getBoolean(key, defValue)
    }
    fun putBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    fun putString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    fun getString(key: String, defValue: String): String {
        return prefs.getString(key, defValue) ?: defValue
    }

}