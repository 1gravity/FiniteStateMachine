package com.airfox.fsm

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.airfox.fsm.util.DaggerFactory
import com.airfox.fsm.util.Logger
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@SuppressLint("CheckResult")
class MainActivity : AppCompatActivity() {

    private var disposables: CompositeDisposable = CompositeDisposable()

    @Inject lateinit var doorModule: DoorModule
    @Inject lateinit var pacmanModule: PacmanModule
    @Inject lateinit var androidModule: AndroidModule

    @Inject lateinit var logger: Logger

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

        doorModule.registerActivity(this)
        pacmanModule.registerActivity(this)
        androidModule.registerActivity(this)

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
                doorModule.start()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_pacman -> {
                pacmanModule.startGame()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_android -> {
                androidModule.start()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

}
