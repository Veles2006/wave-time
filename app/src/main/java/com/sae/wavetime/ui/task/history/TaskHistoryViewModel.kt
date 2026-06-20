package com.sae.wavetime.ui.task.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sae.wavetime.data.mapper.mapToHistoryItems
import com.sae.wavetime.data.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TaskHistoryViewModel(
    private val repository: TaskRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TaskHistoryState())
    val state: StateFlow<TaskHistoryState> = _state

    private var hasLoaded = false

    fun loadHistory() {
        if (hasLoaded) return
        hasLoaded = true

        viewModelScope.launch {
            repository.getAll()
                .catch { e ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = e.message
                        )
                    }
                }
                .collect { tasks ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = null,
                            items = mapToHistoryItems(tasks)
                        )
                    }
                }
        }
    }
}