package com.takohi.sanctuary.ui.restriction

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.takohi.sanctuary.R
import kotlinx.android.synthetic.main.view_restriction.view.*

import java.util.ArrayList

class RestrictionAdapter(private val listener: (Restriction, Boolean) -> Unit) : RecyclerView.Adapter<RestrictionAdapter.ViewHolder>() {
    private val items = ArrayList<Restriction>()

    @SuppressLint("NotifyDataSetChanged")
    fun update(newList: List<Restriction>) {
        items.clear()
        items.addAll(newList)

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.view_restriction, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position], listener)

    override fun getItemCount(): Int = items.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(restriction: Restriction, listener: (Restriction, Boolean) -> Unit) = with(itemView) {
            textView_restriction_name.text = context.getString(restriction.name)
            textView_restriction_description.text = context.getString(restriction.description)

            checkBox_restriction_enabled.setOnCheckedChangeListener(null) // Prevent old listener to be called when changing isChecked state next
            checkBox_restriction_enabled.isChecked = restriction.enabled
            checkBox_restriction_enabled.setOnCheckedChangeListener { buttonView, isChecked ->
                listener(restriction, isChecked)
            }
        }
    }
}
