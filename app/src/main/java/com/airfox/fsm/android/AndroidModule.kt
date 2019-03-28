package com.airfox.fsm.android

import android.annotation.SuppressLint
import android.content.Intent
import com.airfox.fsm.ActivityModule
import javax.inject.Inject

@SuppressLint("CheckResult")
class AndroidModule @Inject constructor() : ActivityModule() {

    override fun start() {
        Intent(activity(), AndroidActivity1::class.java).run {
            activity()?.startActivity(this)
        }
    }

}