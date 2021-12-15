package com.takohi.sanctuary.ui.setup

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.github.paolorotolo.appintro.AppIntro2
import com.takohi.sanctuary.R
import com.takohi.sanctuary.ui.MainActivity


class SetupActivity: AppIntro2() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addSlide(SetupSlideFragment.newInstance(R.layout.fragment_setup_1))

        setBarColor(getColor(R.color.colorPrimary))
    }

    override fun onSkipPressed(currentFragment: Fragment) {
        super.onSkipPressed(currentFragment)
        launchMain()
    }

    override fun onDonePressed(currentFragment: Fragment) {
        super.onDonePressed(currentFragment)
        launchMain()
    }

    private fun launchMain() {
        // Launch activity from Work Profile
        val launchIntent = Intent(this, MainActivity::class.java)
        launchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(launchIntent)
        finishAffinity()
    }
}