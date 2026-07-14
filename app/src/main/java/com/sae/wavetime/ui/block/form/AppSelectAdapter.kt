package com.sae.wavetime.ui.block.form

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.sae.wavetime.R
import com.sae.wavetime.ui.model.AppUiModel

class AppSelectAdapter(
    private val onAppSelected: (AppUiModel) -> Unit
) : ListAdapter<AppUiModel, AppSelectAdapter.AppSelectViewHolder>(
    AppDiffCallback
) {

    private var selectedPackageName: String? = null

    class AppSelectViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        val cardBlock: MaterialCardView =
            itemView.findViewById(R.id.cardBlock)

        val tvName: TextView =
            itemView.findViewById(R.id.tvName)

        val tvPackageName: TextView =
            itemView.findViewById(R.id.tvPackageName)

        val imgApp: ImageView =
            itemView.findViewById(R.id.imgApp)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AppSelectViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_app, parent, false)

        return AppSelectViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: AppSelectViewHolder,
        position: Int
    ) {
        val app = getItem(position)

        bindApp(
            holder = holder,
            app = app
        )
    }

    private fun bindApp(
        holder: AppSelectViewHolder,
        app: AppUiModel
    ) {
        val context = holder.itemView.context
        val isSelected = app.packageName == selectedPackageName

        holder.tvName.text = app.appName
        holder.tvPackageName.text = app.packageName

        holder.imgApp.setImageDrawable(
            app.icon ?: AppCompatResources.getDrawable(
                context,
                R.drawable.default_app
            )
        )

        if (isSelected) {
            holder.cardBlock.setCardBackgroundColor(
                context.getColor(R.color.selected_app_bg)
            )

            holder.cardBlock.strokeColor =
                context.getColor(R.color.primary)

            holder.cardBlock.strokeWidth = 2

            holder.tvName.setTextColor(
                context.getColor(R.color.on_primary)
            )

            holder.tvPackageName.setTextColor(
                context.getColor(R.color.on_primary)
            )

        } else {
            holder.cardBlock.setCardBackgroundColor(
                context.getColor(R.color.card_bg)
            )

            holder.cardBlock.strokeColor =
                context.getColor(android.R.color.transparent)

            holder.cardBlock.strokeWidth = 0

            holder.tvName.setTextColor(
                context.getColor(R.color.text_primary)
            )

            holder.tvPackageName.setTextColor(
                context.getColor(R.color.text_secondary)
            )
        }



        holder.itemView.setOnClickListener {
            val clickedPosition = holder.bindingAdapterPosition

            if (clickedPosition == RecyclerView.NO_POSITION) {
                return@setOnClickListener
            }

            val clickedApp = getItem(clickedPosition)

            selectApp(clickedApp)
        }
    }

    private fun selectApp(app: AppUiModel) {
        if (selectedPackageName == app.packageName) {
            return
        }

        val oldSelectedPackage = selectedPackageName
        selectedPackageName = app.packageName

        val oldPosition = currentList.indexOfFirst { currentApp ->
            currentApp.packageName == oldSelectedPackage
        }

        val newPosition = currentList.indexOfFirst { currentApp ->
            currentApp.packageName == app.packageName
        }

        if (oldPosition != -1) {
            notifyItemChanged(oldPosition)
        }

        if (newPosition != -1) {
            notifyItemChanged(newPosition)
        }

        onAppSelected(app)
    }

    fun clearSelection() {
        val oldPosition = currentList.indexOfFirst { app ->
            app.packageName == selectedPackageName
        }

        selectedPackageName = null

        if (oldPosition != -1) {
            notifyItemChanged(oldPosition)
        }
    }

    companion object {

        private val AppDiffCallback =
            object : DiffUtil.ItemCallback<AppUiModel>() {

                override fun areItemsTheSame(
                    oldItem: AppUiModel,
                    newItem: AppUiModel
                ): Boolean {
                    return oldItem.packageName ==
                            newItem.packageName
                }

                override fun areContentsTheSame(
                    oldItem: AppUiModel,
                    newItem: AppUiModel
                ): Boolean {
                    return oldItem.appName == newItem.appName &&
                            oldItem.packageName == newItem.packageName &&
                            oldItem.isActive == newItem.isActive
                }
            }
    }
}