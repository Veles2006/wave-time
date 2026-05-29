package com.sae.wavetime.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.sae.wavetime.data.repository.TaskRepository

class SplashViewModel(
    private val taskRepository: TaskRepository
) : ViewModel() {

    suspend fun prepareTodayTasks() {
        taskRepository.generateTodayDailyTasks()
    }
}