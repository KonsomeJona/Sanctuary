package com.takohi.sanctuary.ui.preference

import android.os.Bundle
import android.widget.Toast
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.takohi.sanctuary.R
import com.takohi.sanctuary.SanctuaryDeviceAdminReceiver
import com.takohi.sanctuary.managers.NotificationManager
import com.takohi.sanctuary.managers.PasswordManager
import com.takohi.sanctuary.utils.displayAlertDialog

class PreferenceFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)

        findPreference<SwitchPreferenceCompat>("show_lock_notification")!!.setOnPreferenceChangeListener {
            _, newValue -> NotificationManager(requireContext()).showLockNotification(newValue as Boolean); true
        }

        val devicePolicyManager = SanctuaryDeviceAdminReceiver.devicePolicyManager(requireContext())
        val componentName = SanctuaryDeviceAdminReceiver.componentName(requireContext())

        findPreference<SwitchPreferenceCompat>("enable_chrome")!!.isChecked = devicePolicyManager.isApplicationHidden(componentName, "com.android.chrome")
        findPreference<SwitchPreferenceCompat>("enable_chrome")!!.setOnPreferenceChangeListener { preference, newValue ->
            run {
                devicePolicyManager.setApplicationHidden(componentName, "com.android.chrome", newValue as Boolean)
                false // Never commit, we can't rely on it as app will always crash anyway
            }
        }

        findPreference<Preference>("change_password")!!.setOnPreferenceClickListener {
            PasswordManager(requireContext()).changePassword(); true
        }

        findPreference<Preference>("clear_password")!!.setOnPreferenceClickListener {
            val text = if(PasswordManager(requireContext()).resetPassword()) R.string.reset_password_success else R.string.reset_password_error
            Toast.makeText(context, text, Toast.LENGTH_LONG).show()
            true
        }

        findPreference<Preference>("wipe_profile")!!.setOnPreferenceClickListener {
            displayAlertDialog(requireContext(), R.string.preference_wipe_dialog_title, R.string.preference_wipe_dialog_message) { confirmed: Boolean ->
                run {
                    if (confirmed)
                        SanctuaryDeviceAdminReceiver.devicePolicyManager(requireContext()).wipeData(0)
                }
            }
            true
        }
    }
}