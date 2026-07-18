package com.sae.wavetime.ui.task.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sae.wavetime.MainActivity
import com.sae.wavetime.R
import com.sae.wavetime.domain.model.Task
import com.sae.wavetime.ui.common.toHourMinuteSecond
import com.sae.wavetime.ui.model.HistoryListItemUiModel

class HistoryAdapter(
    private val onUndoCompletionClick: (Task) -> Unit,
    private val openTaskDetail : (String) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var items: List<HistoryListItemUiModel> = emptyList()

    fun submitList(newList: List<HistoryListItemUiModel>) {
        items = newList
        notifyDataSetChanged()
    }

    class DateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDate : TextView = itemView.findViewById(R.id.tvDate)

        fun bind(item: HistoryListItemUiModel.DateHeader) {
            tvDate.text = item.date
        }
    }

    class TaskViewHolder(
        itemView: View,
        private val onUndoCompletionClick: (Task) -> Unit,
        private val openTaskDetail : (String) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val tvName: TextView = itemView.findViewById(R.id.tvTaskName)
        private val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        private val btnOptions: ImageButton = itemView.findViewById(R.id.btnOptions)

        fun bind(item: HistoryListItemUiModel.TaskItem) {
            val context = itemView.context
            val task = item.task

            tvName.text = task.name
            tvTime.text = task.lastCompletedAt.toHourMinuteSecond(context)

            btnOptions.setOnClickListener { anchorView ->
                val popupMenu = PopupMenu(context, anchorView)

                popupMenu.menuInflater.inflate(
                    R.menu.menu_task_history_item,
                    popupMenu.menu
                )

                popupMenu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.action_undo_completion -> {
                            onUndoCompletionClick(task)
                            true
                        }

                        else -> false
                    }
                }

                popupMenu.show()
            }

            itemView.setOnClickListener {
                openTaskDetail(task.id)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is HistoryListItemUiModel.DateHeader -> VIEW_TYPE_DATE
            is HistoryListItemUiModel.TaskItem -> VIEW_TYPE_TASK
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            VIEW_TYPE_DATE -> {
                val view = inflater.inflate(R.layout.item_history_date, parent, false)
                DateViewHolder(view)
            }

            VIEW_TYPE_TASK -> {
                val view = inflater.inflate(
                    R.layout.item_history_task,
                    parent,
                    false
                )

                TaskViewHolder(
                    itemView = view,
                    onUndoCompletionClick = onUndoCompletionClick,
                    openTaskDetail = openTaskDetail
                )
            }

            else -> error("Unknown viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is HistoryListItemUiModel.DateHeader -> {
                (holder as DateViewHolder).bind(item)
            }

            is HistoryListItemUiModel.TaskItem -> {
                (holder as TaskViewHolder).bind(item)
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    companion object {
        private const val VIEW_TYPE_DATE = 1
        private const val VIEW_TYPE_TASK = 2
    }
}