package com.takohi.sanctuary.ui.about

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.takohi.sanctuary.BuildConfig
import com.takohi.sanctuary.R
import kotlinx.android.synthetic.main.fragment_about.view.*


class AboutFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(view) {
            textView_about_version.text = BuildConfig.VERSION_NAME
            button_about_privacyPolicy.setOnClickListener {
                val url = getString(R.string.app_privacyPolicyUrl)
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    val text = String.format(getString(R.string.about_privacyPolicy_message), url)
                    Toast.makeText(requireContext(), text, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}