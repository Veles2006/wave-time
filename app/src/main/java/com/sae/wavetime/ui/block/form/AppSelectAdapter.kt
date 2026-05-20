package com.sae.wavetime.ui.block.form

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sae.wavetime.R
import com.sae.wavetime.ui.model.AppUiModel

class AppSelectAdapter(
    private val onAppSelected: (AppUiModel) -> Unit
) : RecyclerView.Adapter<AppSelectAdapter.AppSelectViewHolder>(){
    private var apps: List<AppUiModel> = emptyList()
    private var selectedPackageName: String? = null

    fun submitList(newList: List<AppUiModel>) {
        apps = newList
        notifyDataSetChanged()
    }

    fun setSelectedApp(packageName: String) {
        selectedPackageName = packageName
        notifyDataSetChanged()
    }

    class AppSelectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvPackageName: TextView = itemView.findViewById(R.id.tvPackageName)
        val imgApp: ImageView = itemView.findViewById(R.id.imgApp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppSelectViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_app, parent, false)
        return AppSelectViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppSelectViewHolder, position: Int) {
        val app = apps[position]

        holder.tvName.text = app.appName
        holder.tvPackageName.text = app.packageName
        holder.imgApp.setImageDrawable(app.icon)

        val isSelected = app.packageName == selectedPackageName

        holder.itemView.setBackgroundColor(
            if (isSelected) {
                holder.itemView.context.getColor(R.color.selected_app_bg)
            } else {
                holder.itemView.context.getColor(R.color.white)
            }
        )
        holder.tvName.setTextColor(
            if (isSelected) {
                holder.itemView.context.getColor(R.color.white)
            } else {
                holder.itemView.context.getColor(R.color.black)
            }
        )

        holder.tvPackageName.setTextColor(
            if (isSelected) {
                holder.itemView.context.getColor(R.color.white)
            } else {
                holder.itemView.context.getColor(R.color.black)
            }
        )

        holder.itemView.setOnClickListener {
            selectedPackageName = app.packageName
            notifyDataSetChanged()
            onAppSelected(app)
        }
    }

    override fun getItemCount(): Int {
        return apps.size
    }
}