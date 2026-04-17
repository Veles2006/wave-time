package com.sae.wavetime.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sae.wavetime.data.model.api.Penalty
import com.sae.wavetime.data.model.api.Reward

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
)