package com.airfox.fsm.util

import android.annotation.SuppressLint
import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import java.lang.IllegalStateException

@SuppressLint("StaticFieldLeak")
internal lateinit var theContext: Context

/**
 * The InitProvider automatically initializes some components.
 */
class InitProvider : ContentProvider() {

    override fun onCreate(): Boolean {
        return context?.run {
            theContext = applicationContext
            NetworkState.enable(this)
            true
        } ?: run {
            throw IllegalStateException("Something is seriously wrong: context must NOT be null")
        }
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?,
                       selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        return null
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        return 0
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        return 0
    }

}
