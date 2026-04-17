package com.sae.wavetime.ui.task.detail

import com.sae.wavetime.data.model.api.Task

data class TaskDetailState(
    val isLoading: Boolean = false,
    val task: Task? = null,
    val error: String? = null
)
