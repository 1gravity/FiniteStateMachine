package com.airfox.fsm.android

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.annotation.MainThread
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.airfox.fsm.R
import com.airfox.fsm.android.state.*
import com.airfox.fsm.base.State
import com.airfox.fsm.util.DaggerFactory
import com.airfox.fsm.util.Logger
import com.airfox.fsm.util.NetworkState
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@SuppressLint("CheckResult")
class AndroidActivity1 : AppCompatActivity() {

    @Inject
    lateinit var logger: Logger
    @Inject
    lateinit var stateMachine: AndroidStateMachine

    private lateinit var progressDialogHolder: ProgressDialogHolder

    private var disposables: CompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DaggerFactory.component.inject(this)

        setContentView(R.layout.activity_android1)

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
            is Finish -> finish()
        }
    }

    private fun showSplashScreen() {
        stateMachine.transition(SplashScreenShown(true))
        // todo
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
        AlertDialog.Builder(this)
            .setView(R.layout.offline_dialog_startup)
            .setPositiveButton(R.string.close, null)
            .setOnDismissListener { stateMachine.transition(Exit) }
            .setCancelable(false)
            .show()
    }

    private fun startAuthentication() {
        stateMachine.transition(UserAuthenticated(true))
        // todo
    }

    private fun showAuthError() {
        AlertDialog.Builder(this)
            .setTitle(R.string.authentication_error)
            .setPositiveButton(R.string.close, null)
            .setOnDismissListener { stateMachine.transition(Exit) }
            .setCancelable(false)
            .show()
    }

    private fun initializeApp() {
        stateMachine.transition(Initialized)
        // todo
    }

    override fun onPause() {
        super.onPause()
        progressDialogHolder.dismissDialog()
    }

}
