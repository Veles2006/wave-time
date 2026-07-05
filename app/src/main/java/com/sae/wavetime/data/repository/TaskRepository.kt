package com.sae.wavetime.data.repository


import com.sae.wavetime.data.dao.TaskDao
import com.sae.wavetime.data.dao.TaskTemplateDao
import com.sae.wavetime.data.mapper.toDomain
import com.sae.wavetime.data.mapper.toEntity
import com.sae.wavetime.data.mapper.toTaskTemplateEntity
import com.sae.wavetime.data.model.entity.TaskEntity
import com.sae.wavetime.domain.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.util.UUID

class TaskRepository(
    private val taskDao: TaskDao,
    private val templateDao: TaskTemplateDao
) {
    fun getAll(): Flow<List<Task>> {
        return taskDao
            .getAll()
            .map { list ->
                list.map { it.toDomain() }
            }
    }

    fun getPendingTasks(): Flow<List<Task>> {
        return taskDao
            .getAll()
            .map { list ->
                list
                    .filter { it.status == "pending" || it.status == "in_progress" }
                    .map { it.toDomain() }
            }
    }

    fun getCompletedTasks(): Flow<List<Task>> {
        return taskDao
            .getAll()
            .map { list ->
                list
                    .filter { it.status == "completed" }
                    .map { it.toDomain() }
            }
    }

    fun getTaskByIdFlow(id: String): Flow<Task?> {
        return taskDao.getByIdFlow(id)
            .map { it?.toDomain() }
    }

    fun hasRunningTimerTaskFlow(): Flow<Boolean> {
        val now = System.currentTimeMillis()
        return taskDao.hasRunningTimerTaskFlow(now)
    }

    fun observeRunningTimerTask(): Flow<Task?> {
        return taskDao.observeRunningTimerTask()
            .map { it?.toDomain() }
    }

    suspend fun getTaskById(id: String): Task? {
        return taskDao.getById(id)?.toDomain()
    }

    suspend fun getRunningTimerTask(): Task? {
        val now = System.currentTimeMillis()
        return taskDao.getRunningTimerTask(now)?.toDomain()
    }

    suspend fun hasRunningTimerTask(): Boolean {
        val now = System.currentTimeMillis()
        return taskDao.hasRunningTimerTask(now)
    }

    suspend fun generateTodayDailyTasks() {
        val today = LocalDate.now().toString()

        val templates = templateDao.getActiveDailyTemplates()

        templates.forEach { template ->
            val existing = taskDao.getTaskByTemplateAndDate(
                templateId = template.id,
                date = today
            )

            if (existing == null) {
                taskDao.insert(
                    TaskEntity(
                        id = UUID.randomUUID().toString(),
                        templateId = template.id,
                        name = template.name,
                        description = template.description,
                        status = "pending",
                        type = "daily",
                        completeMode = template.completeMode,
                        reward = template.reward,
                        penalty = template.penalty,
                        requiredDurationMinutes = template.requiredDurationMinutes,
                        startedAt = template.startedAt,
                        finishAt = template.finishAt,
                        deadline = null,
                        date = today,
                        difficulty = template.difficulty
                    )
                )
            }
        }
    }

    suspend fun insertTaskTemplate(task: Task) {
        if (task.type == "daily") {
            templateDao.insert(task.toTaskTemplateEntity())
        }
    }

    suspend fun insertTask(task: Task) {
        taskDao.insert(task.toEntity())
    }

    suspend fun startTimerTask(
        taskId: String,
        startedAt: Long,
        finishAt: Long
    ) {
        taskDao.startTimerTask(
            taskId = taskId,
            startedAt = startedAt,
            finishAt = finishAt
        )
    }

    suspend fun updateFullTask(task: Task) {
        taskDao.updateFull(
            taskId = task.id,
            name = task.name,
            description = task.description,
            status = task.status,
            type = task.type,
            completeMode = task.completeMode,
            reward = task.reward,
            penalty = task.penalty,
            requiredDurationMinutes = task.requiredDurationMinutes,
            startedAt = task.startedAt,
            finishAt = task.finishAt,
            deadline = task.deadline,
            date = task.date,
            difficulty = task.difficulty
        )
    }

    suspend fun changeStatus(taskId: String, status: String, lastCompletedAt: Long? = null) {
        taskDao.changeStatus(
            taskId = taskId,
            status = status,
            lastCompletedAt = lastCompletedAt
        )
    }

    suspend fun softDeleteTask(id: String) {
        taskDao.softDelete(id)
    }


    suspend fun clearTaskTimer(taskId: String) {
        taskDao.clearTaskTimer(taskId)
    }
}