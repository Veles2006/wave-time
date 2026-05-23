package com.sae.wavetime.ui.block.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.materialswitch.MaterialSwitch
import com.sae.wavetime.R
import com.sae.wavetime.ui.model.AppUiModel

class AppAdapter(
    private val onLongClick: (AppUiModel) -> Unit,
    private val onToggleActivity: (id: String, isChecked: Boolean) -> Unit,
    private val openBlockDetail: (String) -> Unit
): RecyclerView.Adapter<AppAdapter.AppViewHolder>() {

    private var apps: List<AppUiModel> = emptyList()

    fun submitList(newList: List<AppUiModel>) {
        apps = newList
        notifyDataSetChanged()
    }

    class AppViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvPackageName: TextView = itemView.findViewById(R.id.tvPackageName)
        val imgApp: ImageView = itemView.findViewById(R.id.imgApp)
        val switchActive: MaterialSwitch = itemView.findViewById(R.id.switchActive)
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
        if (app.icon != null) {
            holder.imgApp.setImageDrawable(app.icon)
        } else {
            holder.imgApp.setImageResource(R.drawable.waifu_2)
        }
        holder.switchActive.visibility = View.VISIBLE
        holder.switchActive.isChecked = app.isActive
        holder.switchActive.setOnCheckedChangeListener { _, isChecked ->
            onToggleActivity(app.id, isChecked)
        }

        holder.itemView.setOnLongClickListener {
            onLongClick(app)
            true
        }

        holder.itemView.setOnClickListener {
            openBlockDetail(app.id)
        }
    }

    override fun getItemCount(): Int {
        return apps.size
    }
}