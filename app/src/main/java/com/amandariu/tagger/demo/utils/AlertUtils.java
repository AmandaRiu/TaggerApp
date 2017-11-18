package com.amandariu.tagger.demo.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.amandariu.tagger.R;

/**
 * Various helper methods for displaying alert messages to the end user.
 *
 * @author amandariu (11/5/17)
 */
public abstract class AlertUtils {

    /**
     * Displays an alert with a title of "Error" and whatever message provided.
     *
     * @param msg The message to display
     */
    public static void displayErrorMessage(@NonNull Context context, @NonNull String msg) {
        String title = context.getResources().getString(R.string.error);
        displayAlertMessage(context, title, msg);
    }

    /**
     * Pop a simple alert dialog to the end-user
     *
     * @param title The title of the alert dialog
     * @param msg   The message to display
     */
    public static void displayAlertMessage(@NonNull Context context,
                                           @Nullable String title, @NonNull String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setMessage(msg)
                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        if (title != null) {
            builder.setTitle(title);
        }

        builder.create().show();
    }
}
