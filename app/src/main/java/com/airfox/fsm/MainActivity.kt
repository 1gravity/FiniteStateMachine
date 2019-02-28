package com.airfox.fsm

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.MainThread
import com.airfox.fsm.base.State
import com.airfox.fsm.ui.main.MainFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import com.airfox.fsm.pacman.ghost.*
import com.airfox.fsm.util.DaggerFactory
import javax.inject.Inject

const val logTag = "FiniteStateMachine"

@SuppressLint("CheckResult")
class MainActivity : AppCompatActivity() {

    private var disposables: CompositeDisposable = CompositeDisposable()

    @Inject lateinit var door1: Door
    @Inject lateinit var door2: Door

    @Inject lateinit var ghost1: Ghost

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_activity)

        DaggerFactory.component.inject(this)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()

            ghost1.transition(Actions.Start)
        }

        Log.i(logTag, "Door 1")
        door1.transition(Close)
        door1.transition(Open)
        door1.transition(Close)
        door1.transition(Open)

        Log.i(logTag, "Door 2")
        door2.transition(Open)
        door2.transition(Close)
        door2.transition(Open)
        door2.transition(Close)
        door2.transition(Actions.PacmanEatsPill)
    }

    override fun onResume() {
        super.onResume()

        ghost1.getStates()
            .distinctUntilChanged()
            .doOnSubscribe { disposables.add(it) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { dispatchState(it) }
    }

    override fun onPause() {
        super.onPause()

        disposables.clear()
    }

    @MainThread
    private fun dispatchState(state: State) {
        Log.i(logTag, "dispatching state: ${state.javaClass.simpleName}")

        when (state) {
            is Chase -> {
                // try to get closer to the Pacman
                val newPos = with(state.position) { Pair(first, second + 1) }
                when (newPos.second) {
                    in 0..10 -> ghost1.transition(Actions.MoveTo(newPos))
                    else -> ghost1.transition(Actions.CollisionWithPacman)
                }
            }
            is Scatter -> {
                // try to get away from the Pacman
                val newPos = state.position
                ghost1.transition(Actions.MoveTo(newPos))
            }
            is Dead -> {
                // ghost is dead -> show some "deadly" animation and remove it from the game
            }
        }
    }
}
