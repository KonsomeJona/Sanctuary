package com.takohi.sanctuary.managers

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.edit
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.preference.PreferenceManager
import com.takohi.sanctuary.R
import com.takohi.sanctuary.SanctuaryDeviceAdminReceiver
import com.takohi.sanctuary.database.SanctuaryDatabase
import com.takohi.sanctuary.database.synchronizeApplications
import kotlinx.coroutines.*

class VaultManager(val context: Context) {
    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val adminComponentName = SanctuaryDeviceAdminReceiver.componentName(context)
    private val devicePolicyManager = SanctuaryDeviceAdminReceiver.devicePolicyManager(context)

    fun lock() {
        // Do not lock again if already locked, applications list might be lost
        if (preferences.getBoolean("is_locked", false))
            return

        LocalBroadcastManager.getInstance(context).sendBroadcast(Intent("exit"))
        preferences.edit {
            putBoolean("is_locked", true)
        }

        NotificationManager(context).showLockNotification(false)

        Toast.makeText(context, R.string.home_locked, Toast.LENGTH_SHORT).show()

        CoroutineScope(Dispatchers.IO).launch {
            synchronizeApplications(context)

            SanctuaryDatabase.getInstance(context).applicationDao().getVisibleOnly().forEach {
                if (it.packageName != "com.android.chrome")
                    devicePolicyManager.setApplicationHidden(adminComponentName, it.packageName, true)
            }

            devicePolicyManager.lockNow()
        }
    }

    fun unlock() {
        if (!preferences.getBoolean("is_locked", false))
            return

        CoroutineScope(Dispatchers.IO).launch {
            SanctuaryDatabase.getInstance(context).applicationDao().getVisibleOnly().forEach {
                devicePolicyManager.setApplicationHidden(adminComponentName, it.packageName, false)
            }

            PreferenceManager.getDefaultSharedPreferences(context).edit {
                putBoolean("is_locked", false)
            }

            withContext(Dispatchers.Main) {
                Toast.makeText(context, R.string.home_unlocked, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}