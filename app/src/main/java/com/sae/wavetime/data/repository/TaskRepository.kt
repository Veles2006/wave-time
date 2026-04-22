package com.sae.wavetime.data.repository


import com.sae.wavetime.data.dao.TaskDao
import com.sae.wavetime.data.mapper.toDomain
import com.sae.wavetime.data.mapper.toDomainList
import com.sae.wavetime.data.mapper.toEntity
import com.sae.wavetime.domain.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskRepository(
    private val taskDao: TaskDao
) {
    fun getTasks(): Flow<List<Task>> {
        return taskDao
            .getAll()
            .map { it.toDomainList() }
    }

    suspend fun insertTask(task: Task) {
        taskDao.insert(task.toEntity())
    }

    suspend fun updateFullTask(task: Task) {
        taskDao.updateFull(
            taskId = task.id,
            name = task.name,
            description = task.description,
            status = task.status,
            reward = task.reward,
            penalty = task.penalty,
            deadline = task.deadline,
            date = task.date,
            difficulty = task.difficulty
        )
    }

    suspend fun changeStatus(taskId: String, status: String) {
        taskDao.changeStatus(
            taskId = taskId,
            status = status
        )
    }

    suspend fun softDeleteTask(id: String) {
        taskDao.softDelete(id)
    }

    fun getTaskByIdFlow(id: String): Flow<Task?> {
        return taskDao.getByIdFlow(id)
            .map { it?.toDomain() }
    }
}