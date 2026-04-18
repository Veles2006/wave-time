package com.sae.wavetime.ui.task.list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sae.wavetime.data.model.api.Task
import com.sae.wavetime.data.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TaskListViewModel(
    private val repository: TaskRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TaskListState())
    val state: StateFlow<TaskListState> = _state

    fun loadTasks() {
        viewModelScope.launch {

            _state.update { it.copy(isLoading = true, error = null) }

            try {
                val tasks: List<Task> = repository.getTasks()

                _state.update {
                    it.copy(
                        isLoading = false,
                        tasks = tasks,
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

    fun addTask(task: Task) {
        viewModelScope.launch {
            repository.insertTask(task)

            // reload lại list sau khi thêm
            loadTasks()
        }
    }
}