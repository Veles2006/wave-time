package com.sae.wavetime.engine.event

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object TaskEventBus {

    private val _event = MutableStateFlow<TaskEvent?>(null)
    val event: StateFlow<TaskEvent?> = _event.asStateFlow()

    fun send(event: TaskEvent) {
        _event.value = event
    }

    fun clear() {
        _event.value = null
    }

    fun clearIfSame(event: TaskEvent) {
        if (_event.value == event) {
            _event.value = null
        }
    }
}

sealed interface TaskEvent {
    data class TaskCompletedByTimer(
        val taskId: String,
        val taskName: String
    ) : TaskEvent
}