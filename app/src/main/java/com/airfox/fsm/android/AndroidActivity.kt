package com.airfox.fsm.android

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.MainThread
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import com.airfox.fsm.android.state.*
import com.airfox.fsm.base.State
import com.airfox.fsm.util.DaggerFactory
import com.airfox.fsm.util.Logger
import com.airfox.fsm.util.NetworkState
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import com.airfox.fsm.R
import com.airfox.fsm.databinding.ActivityAndroidBinding
import com.bumptech.glide.Glide
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

private const val LOGIN_REQUEST = 1
private const val SIGNUP_REQUEST = 2

@SuppressLint("CheckResult")
class AndroidActivity : AppCompatActivity() {

    @Inject lateinit var logger: Logger
    @Inject lateinit var stateMachine: AndroidStateMachine

    private lateinit var progressDialogHolder: ProgressDialogHolder

    private lateinit var binding: ActivityAndroidBinding

    private var disposables: CompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DaggerFactory.component.inject(this)

        binding = ActivityAndroidBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.rootView)

        progressDialogHolder = ProgressDialogHolder(this)

        // listen to state changes
        stateMachine.getStates()
            .distinctUntilChanged()
            .doOnSubscribe { disposables.add(it) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { dispatchState(it) }

        if (savedInstanceState == null) {
            stateMachine.transition(ApplicationStarted)
        }
    }

    @MainThread
    private fun dispatchState(state: State) {
        logger.log("dispatch state: ${state.javaClass.simpleName}")

        // show loading spinner depending on the state
        when (state) {
            is CheckConnection -> progressDialogHolder.showLoadingDialog(R.string.connecting)
            is InitializeApp -> progressDialogHolder.showLoadingDialog(R.string.loading)
            else -> progressDialogHolder.dismissDialog()
        }

        when (state) {
            is Start -> stateMachine.transition(ApplicationStarted)
            is CheckConnection -> wait4Connection()
            is DeviceOffline -> deviceIsOffline()
            is ShowSplashScreen -> showSplashScreen()
            is AuthenticateUser -> startAuthentication()
            is AuthenticationFailed -> showAuthError()
            is InitializeApp -> initializeApp()
            is AppReady -> appIsReady()
            is Finish -> finish()
        }
    }

    private fun showSplashScreen() {
        showAndHide(false, true, false, true)
        binding.tvTitle.text = getString(R.string.splash_screen_title)
        binding.tvSubtitle.text = getString(R.string.splash_screen_msg)
        binding.btnContinue.setOnClickListener {
            stateMachine.transition(SplashScreenShown)
        }
    }

    private fun wait4Connection() {
        NetworkState.online()
            .subscribeOn(Schedulers.computation())
            .timeout(5, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe( {
                stateMachine.transition(DeviceConnected(true))
            }, {
                stateMachine.transition(DeviceConnected(false))
            } )
    }

    private fun deviceIsOffline() {
        val context = ContextThemeWrapper(this, R.style.AppTheme_Dialog)
        AlertDialog.Builder(context)
            .setView(R.layout.offline_dialog)
            .setPositiveButton(R.string.close, null)
            .setOnDismissListener { stateMachine.transition(Exit) }
            .setCancelable(false)
            .show()
    }

    private fun startAuthentication() {
        showAndHide(false, true, true, false)
        binding.tvTitle.text = getString(R.string.welcome_screen_title)
        binding.tvSubtitle.text = getString(R.string.welcome_screen_msg)
        binding.btnLogin.setOnClickListener {
            Intent(this, LoginActivity::class.java).run { startActivityForResult(this, LOGIN_REQUEST) }
            it.setOnClickListener(null)
        }
        binding.btnSignup.setOnClickListener {
            Intent(this, LoginActivity::class.java).run { startActivityForResult(this, SIGNUP_REQUEST) }
            it.setOnClickListener(null)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (resultCode) {
            Activity.RESULT_OK -> {
                when (requestCode) {
                    LOGIN_REQUEST, SIGNUP_REQUEST -> stateMachine.transition(UserAuthenticated(true))
                }
            }
            else -> stateMachine.transition(UserAuthenticated(false))
        }
    }

    private fun showAuthError() {
        AlertDialog.Builder(this)
            .setTitle(R.string.authentication_error)
            .setPositiveButton(R.string.close, null)
            .setOnDismissListener { stateMachine.transition(Exit) }
            .setCancelable(false)
            .show()
    }

    private val msg = arrayListOf(R.string.initializing_1, R.string.initializing_2, R.string.initializing_3, R.string.initializing_4)

    private fun initializeApp() {
        showAndHide(false, false, false, false)
        Observable.intervalRange(0, 5, 0, 2, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { if (it < 4) progressDialogHolder.showOrUpdateLoadingDialog(msg[it.toInt()]) }
            .doOnComplete {
                progressDialogHolder.dismissDialog()
                stateMachine.transition(Initialized)
            }
            .subscribe()
    }

    private fun appIsReady() {
        showAndHide(true, false, false, false)
        binding.tvTitle.text = getString(R.string.screen_title)
        Glide.with(this)
            .load(R.raw.giphy)
            .into(binding.image)
    }

    private fun showAndHide(gif: Boolean, sub: Boolean, login: Boolean, next: Boolean) {
        binding.image.visibility = if (gif) View.VISIBLE else View.INVISIBLE
        binding.tvSubtitle.visibility = if (sub) View.VISIBLE else View.INVISIBLE
        binding.btnLogin.visibility = if (login) View.VISIBLE else View.INVISIBLE
        binding.btnSignup.visibility = if (login) View.VISIBLE else View.INVISIBLE
        binding.btnContinue.visibility = if (next) View.VISIBLE else View.INVISIBLE
    }

    override fun onPause() {
        super.onPause()
        progressDialogHolder.dismissDialog()
    }

}
