package com.airfox.fsm.android

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.airfox.fsm.R
import com.airfox.fsm.util.DaggerFactory
import com.airfox.fsm.util.Logger
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_login.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@SuppressLint("CheckResult")
class LoginActivity : AppCompatActivity() {

    @Inject lateinit var logger: Logger

    private lateinit var progressDialogHolder: ProgressDialogHolder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DaggerFactory.component.inject(this)

        setContentView(R.layout.activity_login)

        progressDialogHolder = ProgressDialogHolder(this)

        setResult(Activity.RESULT_CANCELED)

        btn_login.setOnClickListener {
            Single.fromCallable { verifyLogin() }
                .doOnSubscribe { progressDialogHolder.showLoadingDialog(R.string.logging_in) }
                .delay(1, TimeUnit.SECONDS)
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
        return et_username.text.toString() == "emanuel" && et_password.text.toString() == "password"
    }

    override fun onPause() {
        super.onPause()
        progressDialogHolder.dismissDialog()
    }

}
