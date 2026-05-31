package com.sae.wavetime.ui.task.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sae.wavetime.data.repository.TaskRepository
import com.sae.wavetime.domain.usecase.CompleteTaskUseCase
import com.sae.wavetime.domain.usecase.StartTimerTaskUseCase

class TaskListViewModelFactory(
    private val repository: TaskRepository,
    private val completeTaskUseCase: CompleteTaskUseCase,
    private val startTimerTaskUseCase: StartTimerTaskUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(TaskListViewModel::class.java)) {
            return TaskListViewModel(repository, completeTaskUseCase, startTimerTaskUseCase) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}