package com.airfox.fsm.android

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.airfox.fsm.R
import com.airfox.fsm.databinding.ActivityLoginBinding
import com.airfox.fsm.util.DaggerFactory
import com.airfox.fsm.util.Logger
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@SuppressLint("CheckResult")
class LoginActivity : AppCompatActivity() {

    @Inject lateinit var logger: Logger

    private lateinit var binding: ActivityLoginBinding

    private lateinit var progressDialogHolder: ProgressDialogHolder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DaggerFactory.component.inject(this)

        binding = ActivityLoginBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.rootView)

        progressDialogHolder = ProgressDialogHolder(this)

        setResult(Activity.RESULT_CANCELED)

        binding.btnLogin.setOnClickListener {
            Single.fromCallable { verifyLogin() }
                .doOnSubscribe { progressDialogHolder.showLoadingDialog(R.string.logging_in) }
                .delay(5, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally { progressDialogHolder.dismissDialog() }
                .subscribe( { result ->
                    when (result) {
                        true -> setResult(Activity.RESULT_OK)
                        false -> setResult(Activity.RESULT_CANCELED)
                    }
                    finish()
                }, {
                    setResult(Activity.RESULT_CANCELED)
                    finish()
                } )
        }
    }

    private fun verifyLogin(): Boolean {
        return binding.etUsername.text.toString() == "emanuel" && binding.etPassword.text.toString() == "password"
    }

    override fun onPause() {
        super.onPause()
        progressDialogHolder.dismissDialog()
    }

}
