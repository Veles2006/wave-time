package com.sae.wavetime.ui.task.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.sae.wavetime.R
import com.sae.wavetime.domain.model.Task

class TaskAdapter(
    private val onLongClick: (Task) -> Unit,
    private val openTaskDetail : (String) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {
    private var tasks: List<Task> = emptyList()

    fun submitList(newList: List<Task>) {
        tasks = newList
        notifyDataSetChanged()
    }

    fun getTaskAt(position: Int): Task {
        return tasks[position]
    }
    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardTask: MaterialCardView = itemView.findViewById(R.id.cardTask)
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]

        holder.tvName.text = task.name
        holder.tvDescription.text = task.description ?: ""

        holder.itemView.setOnLongClickListener {
            onLongClick(task)
            true
        }

        holder.itemView.setOnClickListener {
            openTaskDetail(task.id)
        }

        val context = holder.itemView.context

        if (!task.isDeleted && task.status == "in_progress" && task.completeMode == "timer") {
            holder.cardTask.setCardBackgroundColor(
                ContextCompat.getColor(context, R.color.primary)
            )

            holder.tvName.setTextColor(
                ContextCompat.getColor(context, R.color.on_primary)
            )

            holder.tvDescription.setTextColor(
                ContextCompat.getColor(context, R.color.on_primary)
            )
        } else {
            holder.cardTask.setCardBackgroundColor(
                ContextCompat.getColor(context, R.color.card_bg)
            )

            holder.tvName.setTextColor(
                ContextCompat.getColor(context, R.color.text_primary)
            )

            holder.tvDescription.setTextColor(
                ContextCompat.getColor(context, R.color.text_secondary)
            )
        }
    }



    override fun getItemCount(): Int {
        return tasks.size
    }
}