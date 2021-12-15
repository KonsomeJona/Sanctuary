package com.takohi.sanctuary.ui.setup

import android.os.Bundle
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment


class SetupSlideFragment : Fragment() {
    private var layoutResId: Int = 0

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null && requireArguments().containsKey(ARG_LAYOUT_RES_ID))
            layoutResId = requireArguments().getInt(ARG_LAYOUT_RES_ID)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layoutResId, container, false)
    }

    companion object {
        private const val ARG_LAYOUT_RES_ID = "layoutResId"

        fun newInstance(layoutResId: Int): SetupSlideFragment {
            val baseSlide = SetupSlideFragment()

            val args = Bundle()
            args.putInt(ARG_LAYOUT_RES_ID, layoutResId)
            baseSlide.arguments = args

            return baseSlide
        }
    }
}