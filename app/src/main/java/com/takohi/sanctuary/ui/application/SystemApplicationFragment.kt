package com.takohi.sanctuary.ui.application

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.takohi.sanctuary.R
import com.takohi.sanctuary.SanctuaryDeviceAdminReceiver
import com.takohi.sanctuary.database.SanctuaryDatabase
import com.takohi.sanctuary.database.SystemApplication
import com.takohi.sanctuary.database.SystemApplicationDao
import com.takohi.sanctuary.utils.displayAlertDialog
import kotlinx.android.synthetic.main.fragment_application.view.*
import kotlinx.coroutines.launch


class SystemApplicationFragment : Fragment() {
    private lateinit var prefs: SharedPreferences
    private val systemApplicationAdapter = SystemApplicationAdapter(::onEnabledStateChanged)
    private lateinit var systemApplicationDao: SystemApplicationDao

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_system_application, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(view.recyclerView_application) {
            layoutManager = LinearLayoutManager(context)
            adapter = systemApplicationAdapter
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        prefs = (context as Activity).getPreferences(Context.MODE_PRIVATE)
        systemApplicationDao = SanctuaryDatabase.getInstance(context).systemApplicationDao()
    }

    override fun onResume() {
        super.onResume()

        lifecycleScope.launch {
            systemApplicationAdapter.update(systemApplicationDao.getAll())
        }
    }

    private fun onEnabledStateChanged(application: SystemApplication, enabled: Boolean) {
        if(prefs.getBoolean("skip_enableSystemApplication", false)) {
            enableSystemApplication(application, enabled)
        } else
            displayAlertDialog(
                    requireContext(),
                    R.string.systemApplication_dialog_enableSystemApplication_title,
                    R.string.systemApplication_dialog_enableSystemApplication_message
            ) { confirmed: Boolean ->
                run {
                    if (confirmed)
                        enableSystemApplication(application, enabled)
                    else
                        updateList()
                }
            }
    }

    private fun updateList() {
        lifecycleScope.launch {
            systemApplicationAdapter.update(systemApplicationDao.getAll())
        }
    }

    private fun enableSystemApplication(application: SystemApplication, enabled: Boolean) {
        application.enabled = enabled
        lifecycleScope.launch {
            systemApplicationDao.update(application)

            val componentName = SanctuaryDeviceAdminReceiver.componentName(requireContext())
            SanctuaryDeviceAdminReceiver.devicePolicyManager(requireContext()).enableSystemApp(componentName, application.packageName)
            updateList()
        }
    }
}
