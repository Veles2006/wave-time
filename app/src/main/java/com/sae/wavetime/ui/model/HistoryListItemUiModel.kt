package com.sae.wavetime.ui.model

import com.sae.wavetime.domain.model.Task

sealed class HistoryListItemUiModel {
    data class DateHeader(
        val date: String
    ) : HistoryListItemUiModel()

    data class TaskItem(
        val task: Task
    ) : HistoryListItemUiModel()
}