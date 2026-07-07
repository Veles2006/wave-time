package com.sae.wavetime.ui.task.form

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sae.wavetime.analytics.AnalyticsLogger
import com.sae.wavetime.data.repository.ItemRepository
import com.sae.wavetime.data.repository.TaskRepository

class TaskFormViewModelFactory(
    private val taskRepo: TaskRepository,
    private val itemRepo: ItemRepository,
    private val analyticsLogger: AnalyticsLogger
): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(TaskFormViewModel::class.java)) {
            return TaskFormViewModel(taskRepo, itemRepo, analyticsLogger) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}