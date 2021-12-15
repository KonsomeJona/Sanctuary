package com.takohi.sanctuary

import android.app.Application
import androidx.core.content.edit
import androidx.preference.PreferenceManager

class SanctuaryApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        PreferenceManager.getDefaultSharedPreferences(this).edit {
            putInt("installed_version", BuildConfig.VERSION_CODE)
        }
    }
}