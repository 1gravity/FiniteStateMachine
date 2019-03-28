package com.airfox.fsm.android

import android.annotation.SuppressLint
import com.airfox.fsm.util.PreferenceUtil

@SuppressLint("CheckResult")
object InitModel {

    fun isSplashScreenShown(): Boolean {
        return PreferenceUtil.getBoolean("SPLASH_SCREEN_SHOWN", false)
    }

    fun setSplashScreenShown() {
        PreferenceUtil.putBoolean("SPLASH_SCREEN_SHOWN", true)
    }

    fun isAuthenticated(): Boolean {
        return PreferenceUtil.getBoolean("USER_AUTHENTICATED", false)
    }

    fun setIsAuthenticated() {
        PreferenceUtil.putBoolean("USER_AUTHENTICATED", true)
    }

    fun isInitialized(): Boolean {
        return PreferenceUtil.getBoolean("APP_INITIALIZED", false)
    }

    fun setIsInitialized() {
        PreferenceUtil.putBoolean("APP_INITIALIZED", true)
    }

}
