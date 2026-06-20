package com.sae.wavetime.data.mapper

import com.sae.wavetime.domain.model.Task
import com.sae.wavetime.ui.model.HistoryListItemUiModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun mapToHistoryItems(tasks: List<Task>): List<HistoryListItemUiModel> {
    return tasks
        .filter { it.status == "completed" && !it.isDeleted }
        .sortedByDescending { it.lastCompletedAt ?: it.createdAt }
        .groupBy { task ->
            task.lastCompletedAt?.let { time ->
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(Date(time))
            } ?: "No date"
        }
        .flatMap { (date, tasksInDate) ->
            listOf(HistoryListItemUiModel.DateHeader(date)) +
                    tasksInDate.map { task ->
                        HistoryListItemUiModel.TaskItem(task)
                    }
        }
}