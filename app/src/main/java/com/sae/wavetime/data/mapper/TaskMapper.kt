package com.sae.wavetime.data.mapper

import com.sae.wavetime.domain.model.Task
import com.sae.wavetime.data.model.entity.TaskEntity
import com.sae.wavetime.data.model.entity.TaskTemplateEntity
import java.util.UUID

fun Task.toEntity(): TaskEntity {
    return TaskEntity(
        id = id,
        name = name,
        description = description,
        status = status,
        type = type,
        completeMode = completeMode,
        reward = reward,
        penalty = penalty,
        requiredDurationMinutes = requiredDurationMinutes,
        startedAt = startedAt,
        finishAt = finishAt,
        deadline = deadline,
        date = date,
        difficulty = difficulty,
        isDeleted = isDeleted,
        createdAt = createdAt,
        lastCompletedAt = lastCompletedAt
    )
}

fun TaskEntity.toDomain(): Task {
    return Task(
        id = id,
        name = name,
        description = description,
        status = status,
        type = type,
        completeMode = completeMode,
        reward = reward,
        penalty = penalty,
        requiredDurationMinutes = requiredDurationMinutes,
        startedAt = startedAt,
        finishAt = finishAt,
        deadline = deadline,
        date = date,
        difficulty = difficulty,
        isDeleted = isDeleted,
        createdAt = createdAt,
        lastCompletedAt = lastCompletedAt
    )
}

fun Task.toTaskTemplateEntity(): TaskTemplateEntity {
    return TaskTemplateEntity(
        id = UUID.randomUUID().toString(),
        name = name,
        description = description,
        repeatType = type,
        completeMode = completeMode,
        reward = reward,
        penalty = penalty,
        requiredDurationMinutes = requiredDurationMinutes,
        startedAt = startedAt,
        finishAt = finishAt,
        deadline = deadline,
        date = date,
        difficulty = difficulty
    )
}

fun List<Task>.toEntityList(): List<TaskEntity> {
    return map { it.toEntity() }
}

fun List<TaskEntity>.toDomainList(): List<Task> {
    return map { it.toDomain() }
}