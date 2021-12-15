package com.takohi.sanctuary.ui.restriction

import android.content.Context
import android.os.UserManager
import com.takohi.sanctuary.R
import com.takohi.sanctuary.SanctuaryDeviceAdminReceiver

fun getRestrictions(context: Context): ArrayList<Restriction> {
    val restrictions = ArrayList<Restriction>()

    restrictions.addAll(listOf(
            ScreenCaptureDisabledRestriction(context),
            CameraDisabledRestriction(context),
            DisallowCrossProfileCopyPasteRestriction(context),
            DisallowShareLocationRestriction(context),
            DisallowUnmuteMicrophoneRestriction(context),
            DisallowDebuggingFeaturesRestriction(context),
            DisallowInstallAppsRestriction(context),
            DisallowUninstallAppsRestriction(context)
    ))

    return restrictions
}

abstract class Restriction(val context: Context) {
    val name = when(val r = context.resources.getIdentifier("restriction_${normalizedName()}_name", "string", context.packageName)) {
        0 -> R.string.unknown
        else -> r
    }

    val description = when(val r = context.resources.getIdentifier("restriction_${normalizedName()}_description", "string", context.packageName)) {
        0 -> R.string.unknown
        else -> r
    }

    abstract var enabled: Boolean

    protected val dpm = SanctuaryDeviceAdminReceiver.devicePolicyManager(context)
    protected val co = SanctuaryDeviceAdminReceiver.componentName(context)

    private fun normalizedName(): String = with(this::class.simpleName) {
        this!![0].toLowerCase() + replace("Restriction", "").substring(1)
    }
}

private abstract class UserRestriction(context: Context): Restriction(context) {
    abstract val userRestriction: String

    override var enabled: Boolean
        get() = dpm.getUserRestrictions(co).getBoolean(userRestriction, false)
        set(value) = if(value) dpm.addUserRestriction(co, userRestriction) else dpm.clearUserRestriction(co, userRestriction)
}

private class ScreenCaptureDisabledRestriction(context: Context) : Restriction(context) {
    override var enabled: Boolean
        get() = dpm.getScreenCaptureDisabled(co)
        set(value) = dpm.setScreenCaptureDisabled(co, value)
}

private class CameraDisabledRestriction(context: Context) : Restriction(context) {
    override var enabled: Boolean
        get() = dpm.getCameraDisabled(co)
        set(value) = dpm.setCameraDisabled(co, value)
}

private class DisallowCrossProfileCopyPasteRestriction(context: Context) : UserRestriction(context) {
    override val userRestriction = UserManager.DISALLOW_CROSS_PROFILE_COPY_PASTE
}

private class DisallowShareLocationRestriction(context: Context) : UserRestriction(context) {
    override val userRestriction = UserManager.DISALLOW_SHARE_LOCATION
}

private class DisallowUnmuteMicrophoneRestriction(context: Context) : UserRestriction(context) {
    override val userRestriction = UserManager.DISALLOW_UNMUTE_MICROPHONE
}

private class DisallowDebuggingFeaturesRestriction(context: Context) : UserRestriction(context) {
    override val userRestriction = UserManager.DISALLOW_DEBUGGING_FEATURES
}

private class DisallowInstallAppsRestriction(context: Context) : UserRestriction(context) {
    override val userRestriction = UserManager.DISALLOW_INSTALL_APPS
}

private class DisallowUninstallAppsRestriction(context: Context) : UserRestriction(context) {
    override val userRestriction = UserManager.DISALLOW_UNINSTALL_APPS
}