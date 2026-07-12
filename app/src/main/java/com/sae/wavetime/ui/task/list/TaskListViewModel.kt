package com.sae.wavetime.ui.task.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sae.wavetime.R
import com.sae.wavetime.data.repository.TaskRepository
import com.sae.wavetime.domain.model.Task
import com.sae.wavetime.domain.usecase.CompleteTaskUseCase
import com.sae.wavetime.domain.usecase.StartTimerTaskUseCase
import com.sae.wavetime.engine.event.TaskEvent
import com.sae.wavetime.engine.event.TaskEventBus
import com.sae.wavetime.ui.common.UiText
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
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
        generateTodayDailyTasks()
        observeTaskEvents()
        observeRunningTimer()
    }

    private fun generateTodayDailyTasks() {
        viewModelScope.launch {
            repository.generateTodayDailyTasks()
        }
    }

    private fun observeTaskEvents() {
        viewModelScope.launch {
            TaskEventBus.event.collect { event ->
                when (event) {
                    is TaskEvent.TaskCompletedByTimer -> {
                        _uiState.update {
                            it.copy(
                                notificationMessage = UiText.StringResource(
                                    resId = R.string.task_completed_notification,
                                    args = listOf(event.taskName)
                                )
                            )
                        }

                        TaskEventBus.clearIfSame(event)
                    }

                    null -> Unit
                }
            }
        }
    }

    private fun observeRunningTimer() {
        viewModelScope.launch {
            combine(
                repository.observeRunningTimerTask(),
                tickerFlow()
            ) { task, now ->

                val finishAt = task?.finishAt

                if (task == null || finishAt == null) {
                    RunningTimerUiState()
                } else {
                    val remaining = finishAt - now

                    RunningTimerUiState(
                        task = task,
                        remainingMillis = remaining.coerceAtLeast(0L),
                        isRunning = remaining > 0
                    )
                }

            }.catch { e ->
                _uiState.update {
                    it.copy(
                        runningTimer = RunningTimerUiState(),
                        error = e.message ?: "Observe timer failed"
                    )
                }
            }.collect { runningTimer ->
                _uiState.update { currentState ->
                    currentState.copy(
                        runningTimer = runningTimer
                    )
                }
            }
        }
    }

    private fun tickerFlow(): Flow<Long> = flow {
        while (currentCoroutineContext().isActive) {
            emit(System.currentTimeMillis())
            delay(1000L)
        }
    }

    fun softDeleteTask(id: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null
                )
            }

            runCatching {
                repository.softDeleteTask(id)
            }.onSuccess {
                _uiState.update {
                    it.copy(
                        isLoading = false
                    )
                }
            }.onFailure { e ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Delete task failed"
                    )
                }
            }
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
                    it.copy(
                        isLoading = false,
                        notificationMessage = if (task.completeMode == "tap") {
                            UiText.StringResource(
                                resId = R.string.task_completed_notification,
                                args = listOf(task.name)
                            )
                        } else {
                            null
                        }
                    )
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

    fun clearNotificationMessage() {
        _uiState.update {
            it.copy(notificationMessage = null)
        }
    }
}