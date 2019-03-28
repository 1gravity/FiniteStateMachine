package com.airfox.fsm.android

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import androidx.annotation.StringRes
import com.airfox.fsm.R

/**
 * Helper class to manage a ProgressDialog.
 */
@SuppressLint("CheckResult")
class ProgressDialogHolder(private val context: Context) {

    private var progressDialog: ProgressDialog? = null

    private fun showLoadingDialog(message: String) {
        dismissDialog()

        progressDialog = (progressDialog ?: ProgressDialog(context, R.style.ProgressDialog))
            .apply{
                isIndeterminate = true
                setCancelable(false)
                setTitle("")
                setMessage(message)
                show()
            }
    }

    fun showLoadingDialog(@StringRes stringResource: Int) {
        showLoadingDialog(context.getString(stringResource))
    }

    fun dismissDialog() {
        progressDialog?.run{
            dismiss()
            progressDialog = null
        }
    }

    fun isProgressDialogShowing(): Boolean {
        return progressDialog?.run{
            isShowing
        } ?: run {
            false
        }
    }

}