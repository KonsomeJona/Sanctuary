package com.takohi.sanctuary.ui.application

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.takohi.sanctuary.R
import com.takohi.sanctuary.database.SystemApplication

import kotlinx.android.synthetic.main.view_application.view.*

import java.util.ArrayList

class SystemApplicationAdapter(private val listener: (SystemApplication, Boolean) -> Unit) : RecyclerView.Adapter<SystemApplicationAdapter.ViewHolder>() {
    private val items = ArrayList<SystemApplication>()

    @SuppressLint("NotifyDataSetChanged")
    fun update(newList: List<SystemApplication>) {
        items.clear()
        items.addAll(newList)

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.view_application, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position], listener)

    override fun getItemCount(): Int = items.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(application: SystemApplication, listener: (SystemApplication, Boolean) -> Unit) = with(itemView) {
            itemView.isEnabled = application.enabled

            // Can't change state if system application is already enabled
            textView_application_label.text = application.label
            textView_application_packageName.text = application.packageName

            checkBox_application_visible.setOnCheckedChangeListener(null) // Prevent old listener to be called when changing isChecked state next
            checkBox_application_visible.isChecked = application.enabled
            checkBox_application_visible.isEnabled = !application.enabled
            checkBox_application_visible.setOnCheckedChangeListener { buttonView, isChecked ->
                listener(application, isChecked)
            }

            with(imageView_application_icon) {
                try {
                    setImageDrawable(itemView.context.packageManager.getApplicationIcon(application.packageName))
                } catch(e: PackageManager.NameNotFoundException) {
                    setImageResource(R.drawable.baseline_visibility_off_24)
                }
            }
        }
    }
}
