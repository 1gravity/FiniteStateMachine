package com.airfox.fsm

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airfox.fsm.android.AndroidModule
import com.airfox.fsm.door.DoorModule
import com.airfox.fsm.gol.GameOfLifeModule
import com.airfox.fsm.pacman.PacmanModule
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
    @Inject lateinit var golModule: GameOfLifeModule
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

        logger.logs()
            .doOnSubscribe { disposables.add(it) }
            .debounce(250, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                rv_message.adapter = MessageAdapter(it)
            }

        doorModule.registerActivity(this)
        pacmanModule.registerActivity(this)
        golModule.registerActivity(this)
        androidModule.registerActivity(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        logger.reset()
        val module = when (item.itemId) {
            R.id.navigation_door -> doorModule
            R.id.navigation_pacman -> pacmanModule
            R.id.navigation_gol -> golModule
            R.id.navigation_android -> androidModule
            else -> null
        }
        return@OnNavigationItemSelectedListener module?.run {
            start()
            true
        }?: false
    }

}

class MessageAdapter(private val messages: List<String>) : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    class ViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val textView = LayoutInflater.from(parent.context)
            .inflate(R.layout.message_view_item, parent, false) as TextView
        return ViewHolder(textView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = messages[position]
    }

    override fun getItemCount() = messages.size

}
