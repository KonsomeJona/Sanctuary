package com.takohi.sanctuary.ui.application

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.takohi.sanctuary.R
import com.takohi.sanctuary.database.Application

import kotlinx.android.synthetic.main.view_application.view.*

import java.util.ArrayList

class ApplicationAdapter(private val listener: (Application, Boolean) -> Unit) : RecyclerView.Adapter<ApplicationAdapter.ViewHolder>() {
    private val items = ArrayList<Application>()

    @SuppressLint("NotifyDataSetChanged")
    fun update(newList: List<Application>) {
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
        fun bind(application: Application, listener: (Application, Boolean) -> Unit) = with(itemView) {
            textView_application_label.text = application.label
            textView_application_packageName.text = application.packageName

            checkBox_application_visible.setOnCheckedChangeListener(null) // Prevent old listener to be called when changing isChecked state next
            checkBox_application_visible.isChecked = !application.hidden
            checkBox_application_visible.setOnCheckedChangeListener { buttonView, isChecked ->
                listener(application, !isChecked)
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
