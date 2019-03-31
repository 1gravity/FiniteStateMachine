package com.airfox.fsm.android

import android.annotation.SuppressLint
import android.content.Intent
import com.airfox.fsm.ActivityModule
import javax.inject.Inject

@SuppressLint("CheckResult")
class AndroidModule @Inject constructor() : ActivityModule() {

    override fun start() {
        Intent(activity(), AndroidActivity::class.java).run {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            activity()?.startActivity(this)
        }
    }

}