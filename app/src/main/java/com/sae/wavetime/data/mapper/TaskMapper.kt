package com.sae.wavetime.data.mapper

import com.sae.wavetime.domain.model.Task
import com.sae.wavetime.data.model.entity.TaskEntity

fun Task.toEntity(): TaskEntity {
    return TaskEntity(
        id = id,
        name = name,
        description = description,
        reward = reward,
        penalty = penalty,
        deadline = deadline,
        date = date,
        difficulty = difficulty,
        status = status,
        isDeleted = isDeleted
    )
}

fun TaskEntity.toDomain(): Task {
    return Task(
        id = id,
        name = name,
        description = description,
        reward = reward,
        penalty = penalty,
        deadline = deadline,
        date = date,
        difficulty = difficulty,
        status = status,
        isDeleted = isDeleted
    )
}

fun List<Task>.toEntityList(): List<TaskEntity> {
    return map { it.toEntity() }
}

fun List<TaskEntity>.toDomainList(): List<Task> {
    return map { it.toDomain() }
}