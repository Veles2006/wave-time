package com.sae.wavetime.ui.block.form

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
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

    class AppSelectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardBlock: MaterialCardView = itemView.findViewById(R.id.cardBlock)
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
        val context = holder.itemView.context

        holder.tvName.text = app.appName
        holder.tvPackageName.text = app.packageName
        holder.imgApp.setImageDrawable(
            app.icon ?: AppCompatResources.getDrawable(
                holder.itemView.context,
                R.drawable.default_app
            )
        )

        val isSelected = app.packageName == selectedPackageName

        if (isSelected) {
            holder.cardBlock.setCardBackgroundColor(
                context.getColor(R.color.selected_app_bg)
            )
            holder.cardBlock.strokeColor = context.getColor(R.color.primary)
            holder.cardBlock.strokeWidth = 2

            holder.tvName.setTextColor(context.getColor(R.color.text_secondary))
            holder.tvPackageName.setTextColor(context.getColor(R.color.text_secondary))
        } else {
            holder.cardBlock.setCardBackgroundColor(
                context.getColor(R.color.card_bg)
            )
            holder.cardBlock.strokeColor = context.getColor(android.R.color.transparent)
            holder.cardBlock.strokeWidth = 0

            holder.tvName.setTextColor(context.getColor(R.color.text_secondary))
            holder.tvPackageName.setTextColor(context.getColor(R.color.text_secondary))
        }

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