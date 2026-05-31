package com.sae.wavetime.ui.task.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sae.wavetime.domain.model.RewardItem
import com.sae.wavetime.data.repository.TaskRepository
import com.sae.wavetime.domain.model.Task
import com.sae.wavetime.domain.usecase.CompleteTaskUseCase
import com.sae.wavetime.domain.usecase.StartTimerTaskUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TaskDetailViewModel(
    private val taskRepo: TaskRepository,
    private val completeTaskUseCase: CompleteTaskUseCase,
    private val startTimerTaskUseCase: StartTimerTaskUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(TaskDetailState())
    val state: StateFlow<TaskDetailState> = _state
    fun observeTask(id: String) {
        viewModelScope.launch {
            taskRepo.getTaskByIdFlow(id)
                .catch { e ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = e.message
                        )
                    }
                }
                .collect { task ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            task = task,
                            error = if (task == null) "Task not found" else null
                        )
                    }
                }
        }
    }
    fun softDeleteTask(id: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                taskRepo.softDeleteTask(id)

                val tasks = taskRepo.getPendingTasks()

                _state.update {
                    it.copy(
                        isLoading = false,
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Unknown error",
                    )
                }
            }
        }
    }
    fun completeTask(task: Task) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            runCatching {
                when (task.completeMode) {
                    "tap" -> {
                        completeTaskUseCase.execute(task.id)
                    }

                    "timer" -> {
                        val finishAt = startTimerTaskUseCase.execute(task)

                        _state.update {
                            it.copy(
                                timerStartEvent = TimerStartEvent(
                                    taskId = task.id,
                                    finishAt = finishAt
                                )
                            )
                        }
                    }

                    else -> {
                        completeTaskUseCase.execute(task.id)
                    }
                }
            }.onSuccess {
                _state.update {
                    it.copy(isLoading = false)
                }
            }.onFailure { e ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Complete task failed"
                    )
                }
            }
        }
    }

    fun clearTimerStartEvent() {
        _state.update {
            it.copy(timerStartEvent = null)
        }
    }
}