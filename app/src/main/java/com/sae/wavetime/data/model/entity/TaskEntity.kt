package com.sae.wavetime.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sae.wavetime.data.model.domain.Penalty
import com.sae.wavetime.data.model.domain.Reward

@Entity(tableName = "tasks")
data class TaskEntity (
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String?,
    val status: String,
    val reward: Reward,
    val penalty: Penalty,
    val deadline: String?,
    val date: String?,
    val difficulty: String,
    val isDeleted: Boolean = false
)