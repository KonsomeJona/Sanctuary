package com.takohi.sanctuary.ui.intro

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.takohi.sanctuary.ui.MainActivity
import com.takohi.sanctuary.ui.provisioning.ProvisioningActivity
import com.takohi.sanctuary.utils.ProvisioningStateUtil

class IntroActivity: Activity(), Runnable {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Handler(Looper.getMainLooper()).postDelayed(this, 1000)
    }

    override fun run() {
        // If open from user profile, setup work profile
        if (!ProvisioningStateUtil.isProfileOwner(this))
            startActivity(Intent(this, ProvisioningActivity::class.java))
        else
            startActivity(Intent(this, MainActivity::class.java))
    }

}