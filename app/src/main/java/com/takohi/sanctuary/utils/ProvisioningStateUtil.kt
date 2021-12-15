package com.takohi.sanctuary.utils

import android.app.admin.DevicePolicyManager
import android.content.Context
import android.provider.Settings

/**
 * Common utility functions used for retrieving information about the provisioning state of the
 * device.
 */
object ProvisioningStateUtil {

    /**
     * @return true if the profile is owned by the DPC
     */
    fun isProfileOwner(context: Context): Boolean {
        val devicePolicyManager = context.getSystemService(
                Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val packageName = context.packageName

        return devicePolicyManager.isProfileOwnerApp(packageName)
    }

}