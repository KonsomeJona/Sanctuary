package com.takohi.sanctuary.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

import com.takohi.sanctuary.managers.VaultManager

class LockVaultReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        VaultManager(context).lock()
    }

}