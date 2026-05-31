package com.sae.wavetime.ui.task.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sae.wavetime.data.repository.TaskRepository
import com.sae.wavetime.domain.model.Task
import com.sae.wavetime.domain.usecase.CompleteTaskUseCase
import com.sae.wavetime.domain.usecase.StartTimerTaskUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TaskListViewModel(
    private val repository: TaskRepository,
    private val completeTaskUseCase: CompleteTaskUseCase,
    private val startTimerTaskUseCase: StartTimerTaskUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        TaskListState(isLoading = true)
    )

    val state: StateFlow<TaskListState> =
        repository.getPendingTasks()
            .catch { e ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Load tasks failed"
                    )
                }

                emit(emptyList())
            }
            .combine(_uiState) { tasks, uiState ->
                uiState.copy(
                    isLoading = false,
                    tasks = tasks
                )
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                TaskListState(isLoading = true)
            )

    init {
        viewModelScope.launch {
            repository.generateTodayDailyTasks()
        }
    }

    fun completeTask(task: Task) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null
                )
            }

            runCatching {
                when (task.completeMode) {
                    "tap" -> {
                        completeTaskUseCase.execute(task.id)
                    }

                    "timer" -> {
                        val finishAt = startTimerTaskUseCase.execute(task)

                        _uiState.update {
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
                _uiState.update {
                    it.copy(isLoading = false)
                }
            }.onFailure { e ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Complete task failed"
                    )
                }
            }
        }
    }

    fun clearTimerStartEvent() {
        _uiState.update {
            it.copy(timerStartEvent = null)
        }
    }
}