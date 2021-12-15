package com.takohi.sanctuary.ui

import android.app.admin.DevicePolicyManager
import android.content.*
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.preference.PreferenceManager
import com.google.android.material.navigation.NavigationBarView
import com.takohi.sanctuary.R
import com.takohi.sanctuary.SanctuaryDeviceAdminReceiver
import com.takohi.sanctuary.managers.NotificationManager
import com.takohi.sanctuary.managers.VaultManager
import com.takohi.sanctuary.ui.about.AboutFragment
import com.takohi.sanctuary.ui.application.ApplicationFragment
import com.takohi.sanctuary.ui.application.SystemApplicationFragment
import com.takohi.sanctuary.ui.home.HomeFragment
import com.takohi.sanctuary.ui.preference.PreferenceFragment
import com.takohi.sanctuary.ui.restriction.RestrictionFragment
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent

import android.R.string.no
import android.net.Uri
import android.widget.Toast


class MainActivity : AppCompatActivity(), NavigationBarView.OnItemSelectedListener {

    lateinit var prefs: SharedPreferences
    private lateinit var adminComponentName: ComponentName
    private lateinit var devicePolicyManager: DevicePolicyManager

    private var fragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        adminComponentName = SanctuaryDeviceAdminReceiver.componentName(this)
        devicePolicyManager = SanctuaryDeviceAdminReceiver.devicePolicyManager(this)

        setSupportActionBar(toolbar)
        with(supportActionBar!!) {
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(false)
        }

        // Bottom navigation
        navigationView_main_bottom.setOnItemSelectedListener(this)

        loadFragment(HomeFragment())

        VaultManager(this).unlock()

        // Show lock notification if enabled in the preferences
        NotificationManager(this).showLockNotification(prefs.getBoolean("show_lock_notification", false))
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(this).registerReceiver(exitReceiver, IntentFilter("exit"))
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(exitReceiver)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return onItemSelected(item.itemId)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return onItemSelected((item.itemId))
    }

    private fun onItemSelected(itemId: Int): Boolean {
        if(itemId == R.id.action_privacyPolicy) {
            displayPrivacyPolicy()
            return true
        }

        val f: Fragment? = when (itemId) {
            R.id.action_home -> HomeFragment()
            R.id.action_applications -> ApplicationFragment()
            R.id.action_restrictions -> RestrictionFragment()
            R.id.action_systemApplications -> SystemApplicationFragment()
            R.id.action_settings -> PreferenceFragment()
            R.id.action_about -> AboutFragment()
            else -> null
        }

        return if (f != null) {
            loadFragment(f)
            true
        } else
            false
    }

    private fun loadFragment(f: Fragment) {
        if (fragment != null && fragment!!::class == f::class)
        // Ignore if same as currently displayed fragment
            return

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame, f)
        transaction.commit()

        fragment = f
    }

    private fun displayPrivacyPolicy() {
        val privacyPolicyUrl = getString(R.string.app_privacyPolicyUrl)

        try {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(privacyPolicyUrl))
            startActivity(browserIntent)
        } catch(e: ActivityNotFoundException) {
            val text = String.format(getString(R.string.about_privacyPolicy_message), privacyPolicyUrl)
            Toast.makeText(this, text, Toast.LENGTH_LONG).show()
        }
    }

    private val exitReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            finishAffinity()
        }
    }
}
