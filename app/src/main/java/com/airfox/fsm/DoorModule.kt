package com.airfox.fsm

import android.annotation.SuppressLint
import io.reactivex.Flowable
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class DoorModule @Inject constructor(private var door: Door) : ActivityModule() {

    @SuppressLint("CheckResult")
    fun start() {
        val random = Random()
        Flowable.intervalRange(1, 10, 0, 500, TimeUnit.MILLISECONDS)
            .map { random.nextInt() % 2 == 0 }
            .subscribe { even ->
                val newState = if (even) Close else Open
                door.transition(newState)
            }
    }

}