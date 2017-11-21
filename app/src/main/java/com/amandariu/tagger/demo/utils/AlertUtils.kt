package com.amandariu.tagger.demo.utils

import android.app.AlertDialog
import android.content.Context
import com.amandariu.tagger.demo.R

/**
 * Displays an alert with a title of "Error" and whatever message provided.
 *
 * @param [context] The active context.
 * @param [msg] The message to display
 */
fun displayErrorMessage(context: Context, msg: String) {
    val title = context.resources.getString(R.string.error)
    displayAlertMessage(context, title, msg)
}

/**
 * Pop a simple alert dialog to the end-user.
 *
 * @param [context] The active context.
 * @param [title] The title of the alert dialog.
 * @param [msg] The message to display.
 */
fun displayAlertMessage(context: Context, title: String?, msg: String) {
    val builder = AlertDialog.Builder(context)
            .setMessage(msg)
            .setNeutralButton(android.R.string.ok) { dialog, _ -> dialog.dismiss() }
    title?.let {
        builder.setTitle(it)
    }
    builder.create().show()
}