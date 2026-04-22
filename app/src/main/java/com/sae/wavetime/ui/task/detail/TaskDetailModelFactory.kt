package com.sae.wavetime.ui.task.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sae.wavetime.data.repository.TaskRepository
import com.sae.wavetime.domain.usecase.CompleteTaskUseCase

class TaskDetailModelFactory(
    private val taskRepo: TaskRepository,
    private val completeTaskUseCase: CompleteTaskUseCase
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(TaskDetailViewModel::class.java)) {
            return TaskDetailViewModel(taskRepo, completeTaskUseCase) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}