package com.airfox.fsm.util

import android.content.Context
import io.reactivex.Completable
import io.reactivex.CompletableEmitter
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import android.net.Network
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.ConnectivityManager.NetworkCallback

object NetworkState : NetworkCallback() {

    private val connectivity by lazy {
        // default value is false
        BehaviorSubject.create<Boolean>().apply { onNext(false) }
    }

    fun enable(context: Context) {
        val networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()

        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.registerNetworkCallback(networkRequest, this)
    }

    fun isOnline(): Boolean = connectivity.blockingFirst()

    fun online(): Completable {
        return Completable.create { emitter ->
            connectivity.filter { it }.subscribe(NetworkObserver(emitter))
        }
    }

    override fun onAvailable(network: Network) {
        connectivity.onNext(true)
    }

    override fun onLost(network: Network) {
        connectivity.onNext(false)
    }

    override fun onUnavailable() {
        connectivity.onNext(false)
    }

}

private class NetworkObserver constructor(private val emitter: CompletableEmitter) : Observer<Boolean> {
    private var disposable: Disposable? = null

    override fun onSubscribe(disposable: Disposable) {
        this.disposable = disposable
    }

    override fun onNext(connectivity: Boolean) {
        if (!this.emitter.isDisposed) {
            this.emitter.onComplete()
        }

        if (this.disposable?.isDisposed == false) {
            this.disposable?.dispose()
        }
    }

    override fun onError(e: Throwable) {}

    override fun onComplete() {}
}
