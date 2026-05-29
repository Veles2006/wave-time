package com.sae.wavetime.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sae.wavetime.domain.model.Penalty
import com.sae.wavetime.domain.model.Reward

@Entity(tableName = "task_templates")
data class TaskTemplateEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String?,
    val repeatType: String = "daily",
    val completeMode: String = "tap",
    val reward: Reward,
    val penalty: Penalty,
    val requiredDurationMinutes: Int? = null,
    val startedAt: Long? = null,
    val finishAt: Long? = null,
    val difficulty: String,
    val deadline: String?,
    val date: String?,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)