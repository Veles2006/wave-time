package com.sae.wavetime.ui.task.history

import com.sae.wavetime.ui.model.HistoryListItemUiModel

data class TaskHistoryState(
    val isLoading: Boolean = false,
    val items: List<HistoryListItemUiModel> = emptyList(),
    val error: String? = null
)