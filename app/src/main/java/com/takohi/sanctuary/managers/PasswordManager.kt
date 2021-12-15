package com.takohi.sanctuary.managers

import android.app.admin.DevicePolicyManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Base64
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.takohi.sanctuary.SanctuaryDeviceAdminReceiver
import com.takohi.sanctuary.utils.VersionUtil
import kotlin.random.Random

class PasswordManager(val context: Context) {
    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val adminComponentName = SanctuaryDeviceAdminReceiver.componentName(context)
    private val devicePolicyManager = SanctuaryDeviceAdminReceiver.devicePolicyManager(context)

    val isPasswordSet: Boolean get() = preferences.getBoolean("password_set", false) && devicePolicyManager.isActivePasswordSufficient

    fun setResetPasswordToken() {
        // On Android 8+, set reset password token if not active
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !devicePolicyManager.isResetPasswordTokenActive(adminComponentName))
            devicePolicyManager.setResetPasswordToken(adminComponentName, getResetToken())
    }

    fun changePassword() {
        preferences.edit {
            putBoolean("password_set", true)
        }

        val action = if(VersionUtil.versionCompare(Build.VERSION.RELEASE, "7.1.0") < 0
                || VersionUtil.versionCompare(Build.VERSION.RELEASE, "7.1.1") > 0)
            DevicePolicyManager.ACTION_SET_NEW_PASSWORD else android.provider.Settings.ACTION_SECURITY_SETTINGS
        ContextCompat.startActivity(context, Intent(action), null)
    }

    fun resetPassword(): Boolean {
        devicePolicyManager.setPasswordQuality(adminComponentName, DevicePolicyManager.PASSWORD_QUALITY_UNSPECIFIED)

        val r: Boolean = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if(devicePolicyManager.isResetPasswordTokenActive(adminComponentName))
                devicePolicyManager.resetPasswordWithToken(adminComponentName, null, getResetToken(), 0)
            else {
                // Try to set again token
                setResetPasswordToken()
                false
            }
        } else
            false // Cannot for Android 7 or less

        devicePolicyManager.setPasswordQuality(adminComponentName, DevicePolicyManager.PASSWORD_QUALITY_SOMETHING)

        if(r) {
            preferences.edit {
                putBoolean("password_set", false)
            }
        }

        return r
    }

    private fun getResetToken(): ByteArray {
        return if(preferences.contains("password_reset_token"))
            Base64.decode(preferences.getString("password_reset_token", null), Base64.NO_WRAP)
        else {
            // Generate new token if not existing
            val token = generateResetPasswordToken()
            preferences.edit {
                putString("password_reset_token", Base64.encodeToString(token, Base64.NO_WRAP))
            }
            token
        }
    }

    private fun generateResetPasswordToken() = Random.nextBytes(32)
}