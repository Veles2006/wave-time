package com.sae.wavetime.ui.task.form

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.sae.wavetime.R
import com.sae.wavetime.ui.common.toCleanItemName
import com.sae.wavetime.ui.common.toTierText
import com.sae.wavetime.ui.model.RewardSelectUiModel

class RewardSelectAdapter(
    private val onIncrease: (RewardSelectUiModel) -> Unit,
    private val onDecrease: (RewardSelectUiModel) -> Unit
) : RecyclerView.Adapter<RewardSelectAdapter.RewardSelectViewHolder>() {
    private var rewards: List<RewardSelectUiModel> = emptyList()

    fun submitList(newList: List<RewardSelectUiModel>) {
        rewards = newList
        notifyDataSetChanged()
    }

    class RewardSelectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvTier: TextView = itemView.findViewById(R.id.tvTier)
        val tvItemCount: TextView = itemView.findViewById(R.id.tvItemCount)
        val btnPlus: MaterialButton = itemView.findViewById(R.id.btnPlus)
        val btnMinus: MaterialButton = itemView.findViewById(R.id.btnMinus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RewardSelectViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reward_select, parent, false)
        return RewardSelectViewHolder(view)
    }

    override fun onBindViewHolder(holder: RewardSelectViewHolder, position: Int) {
        val reward = rewards[position]
        val context = holder.itemView.context

        holder.tvName.text = reward.name
        holder.tvTier.text = reward.tier.toTierText(context)
        holder.tvItemCount.text = "${reward.quantity}"
        holder.btnPlus.setOnClickListener {
            onIncrease(reward)
        }
        holder.btnMinus.setOnClickListener {
            onDecrease(reward)
        }
        if (reward.category == "key" || reward.category == "master_key") {
            holder.tvName.text = reward.name.toCleanItemName()
        }
    }

    override fun getItemCount(): Int {
        return rewards.size
    }
}