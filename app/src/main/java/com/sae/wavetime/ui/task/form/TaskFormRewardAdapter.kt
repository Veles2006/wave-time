package com.sae.wavetime.ui.task.form

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sae.wavetime.R
import com.sae.wavetime.ui.common.toLocalizedKeyName
import com.sae.wavetime.ui.common.toTierText
import com.sae.wavetime.ui.model.RewardSelectUiModel

class TaskFormRewardAdapter : RecyclerView.Adapter<TaskFormRewardAdapter.TaskFormRewardViewHolder>() {
    private var rewards: List<RewardSelectUiModel> = emptyList()

    fun submitList(newList: List<RewardSelectUiModel>) {
        rewards = newList
        notifyDataSetChanged()
    }

    class TaskFormRewardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvTier: TextView = itemView.findViewById(R.id.tvTier)
        val tvQuantity: TextView = itemView.findViewById(R.id.tvQuantity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskFormRewardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task_form_reward, parent, false)
        return TaskFormRewardViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskFormRewardViewHolder, position: Int) {
        val reward = rewards[position]
        val context = holder.itemView.context

        holder.tvName.text = reward.name.toLocalizedKeyName(context)
        holder.tvTier.text = context.getString(
            R.string.tier_format,
            reward.tier.toTierText(context)
        )
        holder.tvQuantity.text = "x${reward.quantity}"
    }

    override fun getItemCount(): Int {
        return rewards.size
    }
}