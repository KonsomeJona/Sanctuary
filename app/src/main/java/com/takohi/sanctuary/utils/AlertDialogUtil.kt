package com.takohi.sanctuary.utils

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.takohi.sanctuary.R
import kotlinx.android.synthetic.main.layout_do_not_show_again.view.*


fun displayAlertDialog(context: Context, titleResId: Int, messageResId: Int, callback: (confirmed: Boolean) -> Unit) {
    val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    val doNotShowPreferenceName = "dialog_notShow_$titleResId"
    if(preferences.getBoolean(doNotShowPreferenceName, false)) {
        callback(true)
        return
    }

    val builder = AlertDialog.Builder(context)
    val view = View.inflate(context, R.layout.layout_do_not_show_again, null)
    builder.setView(view)
    builder.setTitle(titleResId)
    builder.setMessage(messageResId)
    builder.setPositiveButton(android.R.string.ok) { dialog, which ->
        preferences.edit {
            // Store if we should still display dialog next time or not
            putBoolean(doNotShowPreferenceName, view.checkBox_skip.isChecked)
        }

        callback(true)
    }

    builder.setNegativeButton(android.R.string.cancel) { dialog, which ->
        callback(false)
    }
    builder.setCancelable(false)

    val alertDialog = builder.create()
    alertDialog.setOnShowListener {
        // Add margin between buttons
        val posButton = (alertDialog as AlertDialog).getButton(DialogInterface.BUTTON_POSITIVE)
        val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(32, 0, 0, 0)
        posButton.layoutParams = params
    }
    alertDialog.show()
}