package com.takohi.sanctuary.ui.restriction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.takohi.sanctuary.R
import kotlinx.android.synthetic.main.fragment_restriction.view.*

class RestrictionFragment : Fragment() {
    private val restrictionAdapter = RestrictionAdapter(::onRestrictionStateChanged)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_restriction, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(view.recyclerView_restriction) {
            layoutManager = LinearLayoutManager(context)
            adapter = restrictionAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        restrictionAdapter.update(getRestrictions(requireContext()))
    }

    private fun onRestrictionStateChanged(restriction: Restriction, enabled: Boolean) {
        restriction.enabled = enabled
    }
}
