package com.sae.wavetime.data.repository


import com.sae.wavetime.data.dao.TaskDao
import com.sae.wavetime.data.mapper.toDomainList
import com.sae.wavetime.data.mapper.toEntity
import com.sae.wavetime.data.model.api.Penalty
import com.sae.wavetime.data.model.api.Reward
import com.sae.wavetime.data.model.api.Task

class TaskRepository(
    private val taskDao: TaskDao
) {
    suspend fun getTasks(): List<Task> {
        return taskDao
            .getAll()
            .toDomainList()
    }
    suspend fun insertTask(task: Task) {
        taskDao.insert(task.toEntity())
    }
}