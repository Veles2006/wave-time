package com.sae.wavetime.ui.task.detail

import com.sae.wavetime.domain.model.Task

data class TaskDetailState(
    val isLoading: Boolean = false,
    val task: Task? = null,
    val error: String? = null,

    val timerStartEvent: TimerStartEvent? = null
)

data class TimerStartEvent(
    val taskId: String,
    val finishAt: Long
)
