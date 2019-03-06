package com.airfox.fsm

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.annotation.MainThread
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.airfox.fsm.base.State
import com.airfox.fsm.pacman.Collision
import com.airfox.fsm.pacman.MoveTo
import com.airfox.fsm.pacman.Start
import com.airfox.fsm.pacman.ghost.Chase
import com.airfox.fsm.pacman.ghost.Dead
import com.airfox.fsm.pacman.ghost.Ghost
import com.airfox.fsm.pacman.ghost.Scatter
import com.airfox.fsm.pacman.pacman.Pacman
import com.airfox.fsm.util.DaggerFactory
import com.airfox.fsm.util.Logger
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@SuppressLint("CheckResult")
class MainActivity : AppCompatActivity() {

    private var disposables: CompositeDisposable = CompositeDisposable()

    @Inject lateinit var logger: Logger

    @Inject lateinit var door: Door

    @Inject lateinit var pacman: Pacman

    @Inject lateinit var blinky: Ghost
    @Inject lateinit var pinky: Ghost
    @Inject lateinit var inky: Ghost
    @Inject lateinit var clyde: Ghost

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DaggerFactory.component.inject(this)

        setContentView(R.layout.activity_main)

        // bottom navigation
        nav_view.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        // RecyclerView for messages
        val messages = ArrayList<String>()
        rv_message.adapter = MessageAdapter(messages)
        rv_message.layoutManager = LinearLayoutManager(this)
        rv_message.setHasFixedSize(true)

        logger.logs()
            .doOnSubscribe { disposables.add(it) }
            .debounce(250, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                rv_message.adapter = MessageAdapter(it)
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        logger.reset()
        when (item.itemId) {
            R.id.navigation_door -> {
                doorFSM()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_pacman -> {
                pacmanFSM()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_android -> {
                androidFSM()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun doorFSM() {
        val random = Random()
        Flowable.intervalRange(1, 10, 0, 500, TimeUnit.MILLISECONDS)
            .map { random.nextInt() % 2 == 0 }
            .subscribe { even ->
                val newState = if (even) Close else Open
                door.transition(newState)
            }
    }

    private fun pacmanFSM() {
        pacman.transition(Start)
        blinky.transition(Start)
        pinky.transition(Start)
        inky.transition(Start)
        clyde.transition(Start)
        observeGhost(blinky)
        observeGhost(pinky)
        observeGhost(inky)
        observeGhost(clyde)
    }

    private fun androidFSM() {

    }

    private fun observeGhost(ghost: Ghost) {
        ghost.getStates()
            .doOnSubscribe { disposables.add(it) }
            .distinctUntilChanged()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { dispatchState(ghost ,it) }
    }

    @MainThread
    private fun dispatchState(ghost: Ghost, state: State) {
        logger.log("dispatching state: ${state.javaClass.simpleName}")

        when (state) {
            is Chase -> {
                // try to get closer to the Pacman
                val newPos = with(state.position) { Pair(first, second + 1) }
                when (newPos.second) {
                    in 0..10 -> ghost.transition(MoveTo(newPos))
                    else -> ghost.transition(Collision)
                }
            }
            is Scatter -> {
                // try to get away from the Pacman
                val newPos = state.position
                ghost.transition(MoveTo(newPos))
            }
            is Dead -> {
                // ghost is dead -> show some "deadly" animation and remove it from the game
            }
        }
    }
}
