package com.takohi.sanctuary

import android.annotation.SuppressLint
import android.app.admin.DeviceAdminReceiver
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.UserManager
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.takohi.sanctuary.database.SanctuaryDatabase
import com.takohi.sanctuary.database.SystemApplication
import com.takohi.sanctuary.managers.PasswordManager
import com.takohi.sanctuary.ui.setup.SetupActivity
import kotlinx.coroutines.*
import java.util.*

class SanctuaryDeviceAdminReceiver : DeviceAdminReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        val action = intent.action
        if (DevicePolicyManager.ACTION_MANAGED_PROFILE_PROVISIONED == action || Intent.ACTION_MANAGED_PROFILE_ADDED == action) {
            PreferenceManager.getDefaultSharedPreferences(context).edit {
                putBoolean("is_provisioned", true)
            }
        }
    }

    override fun onProfileProvisioningComplete(context: Context, intent: Intent) {
        super.onProfileProvisioningComplete(context, intent)

        val componentName = componentName(context)
        val devicePolicyManager = devicePolicyManager(context)

        devicePolicyManager.setProfileName(componentName, context.getString(R.string.app_name))
        devicePolicyManager.setProfileEnabled(componentName)

        // Help us to detect if a password is set or not with isActivePasswordSufficient
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S)
            devicePolicyManager.setPasswordQuality(componentName, DevicePolicyManager.PASSWORD_QUALITY_SOMETHING)
        else
            devicePolicyManager.requiredPasswordComplexity = DevicePolicyManager.PASSWORD_COMPLEXITY_LOW

        // Work profile can only be deleted from the application by default
        // Removed, dangerous in case user forgot his password
        // devicePolicyManager.addUserRestriction(componentName, UserManager.DISALLOW_REMOVE_MANAGED_PROFILE)

        // Force user to choose a different password for the work profile
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
            devicePolicyManager.addUserRestriction(componentName, UserManager.DISALLOW_UNIFIED_PASSWORD)

        // Set reset password token now for later
        with(PasswordManager(context)) {
            setResetPasswordToken()
            resetPassword()
        }

        // Enable default system applications
        val systemApps = arrayOf("com.android.chrome")
        for (systemApp in systemApps) {
            try {
                devicePolicyManager.enableSystemApp(componentName, systemApp)
            } catch (e: NullPointerException) {
            } catch (e: IllegalArgumentException) {
            }
        }

        // TODO move that somewhere else
        fetchSystemApplications(context)

        // On SDK >= 26, SetupActivity will be launched with activity intent android.app.action.PROVISIONING_SUCCESSFUL
        if(Build.VERSION.SDK_INT < 26) {
            val launchIntent = Intent(context, SetupActivity::class.java)
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(launchIntent)
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun fetchSystemApplications(context: Context) {
        // Fetch disabled system apps
        val disabledSystemApplications = ArrayList<SystemApplication>()
        val packageManager = context.packageManager
        val apps = packageManager.getInstalledApplications(PackageManager.MATCH_SYSTEM_ONLY or PackageManager.MATCH_UNINSTALLED_PACKAGES or PackageManager.MATCH_DISABLED_COMPONENTS)
                .filter {
                    // Trick to know if a system app we can launch or not
                    try {
                        packageManager.getApplicationIcon(it.packageName)
                        false
                    } catch (e: PackageManager.NameNotFoundException) {
                        true
                    }
                }
        apps.forEach {
            disabledSystemApplications.add(
                    SystemApplication(
                            packageName = it.packageName,
                            label = it.loadLabel(context.packageManager).toString()
                    )
            )
        }

        CoroutineScope(Dispatchers.IO).launch {
            SanctuaryDatabase.getInstance(context).systemApplicationDao()
                .insertAll(disabledSystemApplications)
        }
    }

    companion object {
        fun componentName(context: Context): ComponentName =
                ComponentName(context.applicationContext, SanctuaryDeviceAdminReceiver::class.java)

        fun devicePolicyManager(context: Context): DevicePolicyManager =
                context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
    }
}
