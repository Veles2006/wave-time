package com.sae.wavetime.ui.task.create

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sae.wavetime.R
import com.sae.wavetime.ui.model.RewardUiModel

class TaskRewardAdapter : RecyclerView.Adapter<TaskRewardAdapter.TaskRewardViewHolder>() {
    private var rewards: List<RewardUiModel> = emptyList()

    fun submitList(newList: List<RewardUiModel>) {
        rewards = newList
        notifyDataSetChanged()
    }

    class TaskRewardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvQuantity: TextView = itemView.findViewById(R.id.tvQuantity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskRewardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task_reward, parent, false)
        return TaskRewardViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskRewardViewHolder, position: Int) {
        val reward = rewards[position]

        holder.tvName.text = reward.name
        holder.tvQuantity.text = "X${reward.quantity}"
    }

    override fun getItemCount(): Int {
        return rewards.size
    }
}