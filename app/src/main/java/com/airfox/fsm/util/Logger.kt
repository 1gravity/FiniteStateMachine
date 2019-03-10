package com.airfox.fsm.util

import android.util.Log
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject
import javax.inject.Singleton

interface Logger {
    fun log(msg: String)
    fun log(): Observable<String>
    fun logs(): Observable<List<String>>
    fun reset()
}

private const val logTag = "FiniteStateMachine"

@Singleton
class LoggerImpl @Inject constructor(): Logger {
    private val log by lazy { PublishSubject.create<String>() }

    private val logCollection = ArrayList<String>()

    private val logs by lazy { PublishSubject.create<List<String>>() }

    override fun log(): Observable<String> {
        return log
    }

    override fun logs(): Observable<List<String>> {
        return logs
    }

    override fun log(msg: String) {
        Log.i(logTag, msg)
        log.onNext(msg)
        synchronized(logCollection) {
            logCollection.add(msg)
        }
        logs.onNext(logCollection)
    }

    override fun reset() {
        logCollection.clear()
    }

}