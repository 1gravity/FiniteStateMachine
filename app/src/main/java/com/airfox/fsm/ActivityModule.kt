package com.airfox.fsm

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.lang.ref.WeakReference

/**
 * This class is used to implement a "module" dealing with a specific functionality that has to be
 * done on Activity level (compared to controller functionality).
 *
 * It offers some common functionality and keeps only a weak reference to the Activity to prevent
 * memory leaks.
 */
abstract class ActivityModule : LifecycleObserver {

    private var disposables: CompositeDisposable = CompositeDisposable()

    private lateinit var mActivity: WeakReference<AppCompatActivity>

    fun registerActivity(activity: AppCompatActivity) {
        this.mActivity = WeakReference(activity)
        activity.lifecycle.addObserver(this)
    }

    abstract fun start()

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    @CallSuper
    open fun onPause() {
        disposables.clear()
    }

    fun activity(): FragmentActivity? {
        return mActivity.get()
    }

    fun addDisposable(disposable: Disposable) {
        disposables.add(disposable)
    }

    fun dispose() {
        disposables.clear()
    }

}
