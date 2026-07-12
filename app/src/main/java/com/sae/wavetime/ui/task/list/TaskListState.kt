package com.sae.wavetime.ui.task.list

import com.sae.wavetime.domain.model.Task
import com.sae.wavetime.ui.common.UiText

// List<Task> ở đây Task model sẽ được refactor sau
data class TaskListState(
    val isLoading: Boolean = false,
    val tasks: List<Task> = emptyList(),
    val error: String? = null,
    // Timer đang chạy để UI hiển thị countdown
    val runningTimer: RunningTimerUiState = RunningTimerUiState(),

    // Event dùng 1 lần khi vừa start timer
    val timerStartEvent: TimerStartEvent? = null,

    // Message dùng cho Snackbar / Toast
    val notificationMessage: UiText? = null
)

data class RunningTimerUiState(
    val task: Task? = null,
    val remainingMillis: Long = 0L,
    val isRunning: Boolean = false
)

data class TimerStartEvent(
    val taskId: String,
    val finishAt: Long
)