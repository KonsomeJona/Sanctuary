package com.takohi.sanctuary.ui.application

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.takohi.sanctuary.R
import com.takohi.sanctuary.SanctuaryDeviceAdminReceiver
import com.takohi.sanctuary.database.Application
import com.takohi.sanctuary.database.ApplicationDao
import com.takohi.sanctuary.database.SanctuaryDatabase
import com.takohi.sanctuary.database.synchronizeApplications
import com.takohi.sanctuary.utils.displayAlertDialog
import kotlinx.android.synthetic.main.fragment_application.view.*
import kotlinx.coroutines.launch


class ApplicationFragment : Fragment() {
    private lateinit var prefs: SharedPreferences
    private val applicationAdapter = ApplicationAdapter(::onHiddenStateChanged)
    private lateinit var applicationDao: ApplicationDao

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_application, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(view.recyclerView_application) {
            layoutManager = LinearLayoutManager(context)
            adapter = applicationAdapter
        }

        view.button_application_add.setOnClickListener {
            displayAlertDialog(
                    requireContext(),
                    R.string.application_dialog_addApplication_title,
                    R.string.application_dialog_addApplication_message
            ) { confirmed: Boolean ->
                run {
                    if(confirmed) {
                        val context = requireContext()
                        val launchIntent = context.packageManager.getLaunchIntentForPackage("com.android.vending")
                        if (launchIntent != null)
                            context.startActivity(launchIntent)
                        else
                            Toast.makeText(context, R.string.application_googlePlayNotFound, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        prefs = (context as Activity).getPreferences(Context.MODE_PRIVATE)
        applicationDao = SanctuaryDatabase.getInstance(context).applicationDao()
    }

    override fun onResume() {
        super.onResume()

        lifecycleScope.launch {
            synchronizeApplications(requireContext())
            applicationAdapter.update(applicationDao.getAll())
        }
    }

    private fun onHiddenStateChanged(application: Application, isHidden: Boolean) {
        if(application.packageName == "com.android.chrome") {
            Toast.makeText(context, R.string.application_notPermitted, Toast.LENGTH_SHORT).show()
            updateList()
            return
        }

        if(!isHidden || prefs.getBoolean("skip_changeApplicationHiddenState", false))
            setApplicationHidden(application, isHidden)
        else
            displayAlertDialog(
                    requireContext(),
                    R.string.application_dialog_changeApplicationHiddenState_title,
                    R.string.application_dialog_changeApplicationHiddenState_message
            ) { confirmed: Boolean ->
                run {
                    if (confirmed)
                        setApplicationHidden(application, isHidden)
                    else
                        updateList()
                }
            }
    }

    private fun updateList() {
        lifecycleScope.launch {
            applicationAdapter.update(applicationDao.getAll())
        }
    }

    private fun setApplicationHidden(application: Application, isHidden: Boolean) {
        val componentName = SanctuaryDeviceAdminReceiver.componentName(requireContext())
        SanctuaryDeviceAdminReceiver.devicePolicyManager(requireContext()).setApplicationHidden(componentName, application.packageName, isHidden)

        application.hidden = isHidden
        lifecycleScope.launch {
            applicationDao.update(application)
            updateList()
        }
    }
}
