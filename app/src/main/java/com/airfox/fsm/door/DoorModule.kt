package com.airfox.fsm.door

import android.annotation.SuppressLint
import com.airfox.fsm.ActivityModule
import io.reactivex.rxjava3.core.Flowable
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.random.Random

class DoorModule @Inject constructor(private var door: Door) : ActivityModule() {

    @SuppressLint("CheckResult")
    override fun start() {
        Flowable.intervalRange(1, 10, 0, 500, TimeUnit.MILLISECONDS)
            .map { Random.nextInt() % 2 == 0 }
            .map { even -> if (even) Close else Open }
            .subscribe { newState ->
                door.transition(newState)
            }
    }

}