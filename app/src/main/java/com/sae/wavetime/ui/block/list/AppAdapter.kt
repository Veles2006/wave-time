package com.sae.wavetime.ui.block.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sae.wavetime.R
import com.sae.wavetime.ui.model.AppUiModel

class AppAdapter: RecyclerView.Adapter<AppAdapter.AppViewHolder>() {

    private var apps: List<AppUiModel> = emptyList()

    fun submitList(newList: List<AppUiModel>) {
        apps = newList
        notifyDataSetChanged()
    }

    class AppViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvPackageName: TextView = itemView.findViewById(R.id.tvPackageName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_app, parent, false)
        return AppViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        val app = apps[position]

        holder.tvName.text = app.appName
        holder.tvPackageName.text = app.packageName
    }

    override fun getItemCount(): Int {
        return apps.size
    }
}