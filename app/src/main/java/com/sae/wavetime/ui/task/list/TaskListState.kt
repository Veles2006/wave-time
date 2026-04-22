package com.sae.wavetime.ui.task.list

import com.sae.wavetime.domain.model.Task

// List<Task> ở đây Task model sẽ được refactor sau
data class TaskListState(
    val isLoading: Boolean = false,
    val tasks: List<Task> = emptyList(),
    val error: String? = null,
)