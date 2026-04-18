package com.sae.wavetime.ui.task.list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sae.wavetime.data.model.api.Task
import com.sae.wavetime.data.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TaskListViewModel(
    private val repository: TaskRepository
) : ViewModel() {

    val state: StateFlow<TaskListState> =
        repository.getTasks() // Flow<List<Task>>
            .map { tasks ->
                TaskListState(
                    isLoading = false,
                    tasks = tasks
                )
            }
            .catch { e ->
                emit(
                    TaskListState(
                        isLoading = false,
                        error = e.message
                    )
                )
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                TaskListState(isLoading = true)
            )

}