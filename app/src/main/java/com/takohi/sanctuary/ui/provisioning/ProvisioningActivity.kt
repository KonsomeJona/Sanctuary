package com.takohi.sanctuary.ui.provisioning

import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.takohi.sanctuary.R
import com.takohi.sanctuary.SanctuaryDeviceAdminReceiver
import kotlinx.android.synthetic.main.fragment_provisioning.*


class ProvisioningActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.fragment_provisioning)

        textView_provisioning_text.movementMethod = ScrollingMovementMethod()

        button_provisioning_next.setOnClickListener {
            setupWorkProfile()
        }

        button_provisioning_uninstall.setOnClickListener {
            val intent = Intent(Intent.ACTION_DELETE)
            intent.data = Uri.parse("package:$packageName")
            startActivity(intent)
        }

        // If was already provisioned in the past, show another message
        if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("is_provisioned", false)) {
            layout_setup.visibility = View.GONE
            layout_provisioning_alreadyProvisioned.visibility = View.VISIBLE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_PROVISION_MANAGED_PROFILE -> if (resultCode == Activity.RESULT_OK) {
                // Work Profile has been successfully setup
                Toast.makeText(this, R.string.provisioning, Toast.LENGTH_LONG).show()
                finishAffinity()
            } else {
                layout_setup.visibility = View.GONE
                layout_provisioning_somethingWrong.visibility = View.VISIBLE
            }
        }
    }

    private fun setupWorkProfile() {
        val intent = Intent(DevicePolicyManager.ACTION_PROVISION_MANAGED_PROFILE)

        intent.putExtra(DevicePolicyManager.EXTRA_PROVISIONING_DEVICE_ADMIN_COMPONENT_NAME,
                    SanctuaryDeviceAdminReceiver.componentName(this))
        intent.putExtra(DevicePolicyManager.EXTRA_PROVISIONING_LEAVE_ALL_SYSTEM_APPS_ENABLED, true)
        intent.putExtra(DevicePolicyManager.EXTRA_PROVISIONING_SKIP_ENCRYPTION, true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            intent.putExtra(DevicePolicyManager.EXTRA_PROVISIONING_SKIP_USER_CONSENT, true)

        val logoUri = Uri.parse("android.resource://" + packageName + "/" + R.drawable.ic_appintro_arrow_back_white)
        intent.putExtra(DevicePolicyManager.EXTRA_PROVISIONING_LOGO_URI, logoUri)
        intent.putExtra(DevicePolicyManager.EXTRA_PROVISIONING_MAIN_COLOR, getColor(R.color.colorPrimary))

        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        startActivityForResult(intent, REQUEST_PROVISION_MANAGED_PROFILE)
    }

    companion object {
        private const val REQUEST_PROVISION_MANAGED_PROFILE = 1
    }
}
