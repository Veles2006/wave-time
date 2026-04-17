package com.sae.wavetime.data.repository


import com.sae.wavetime.data.dao.TaskDao
import com.sae.wavetime.data.mapper.toDomain
import com.sae.wavetime.data.mapper.toDomainList
import com.sae.wavetime.data.mapper.toEntity
import com.sae.wavetime.data.model.api.Penalty
import com.sae.wavetime.data.model.api.Reward
import com.sae.wavetime.data.model.api.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

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

    fun getTaskByIdFlow(id: String): Flow<Task?> {
        return taskDao.getByIdFlow(id)
            .map { it?.toDomain() }
    }
}