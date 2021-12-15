package com.takohi.sanctuary.ui.home

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.takohi.sanctuary.R
import com.takohi.sanctuary.managers.PasswordManager
import com.takohi.sanctuary.managers.VaultManager
import com.takohi.sanctuary.ui.MainActivity
import com.takohi.sanctuary.utils.displayAlertDialog
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*

class HomeFragment : Fragment() {
    private lateinit var prefs: SharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        button_home_setPassword.setOnClickListener {
            PasswordManager(requireContext()).changePassword()
        }

        button_home_lock.setOnClickListener {
            if (prefs.getBoolean("skip_lockVault", false))
                VaultManager(requireContext()).lock()
            else
                displayAlertDialog(requireContext(), R.string.home_lock_dialog_title, R.string.home_lock_dialog_message) { confirmed: Boolean ->
                    run {
                        if (confirmed)
                            VaultManager(requireContext()).lock()
                    }
                }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        prefs = (context as MainActivity).prefs
    }

    override fun onResume() {
        super.onResume()

        // Hide password dialog if password is set
        view?.cardView_home_password?.visibility = when (PasswordManager(requireContext()).isPasswordSet) {
            true -> View.GONE
            else -> View.VISIBLE
        }
    }

}
