package com.sae.wavetime.data.repository


import com.sae.wavetime.data.model.Penalty
import com.sae.wavetime.data.model.Reward
import com.sae.wavetime.data.model.Task

class TaskRepository {

    suspend fun getTasks(): List<Task> {

        return listOf(
            Task(
                id = "1",
                name = "Study Kotlin",
                description = "Complete MVVM setup",
                reward = Reward(exp = 100),
                penalty = Penalty(),
                deadline = null,
                date = "2026-02-19",
                difficulty = "mortal"
            ),
            Task(
                id = "2",
                name = "Do Exercise",
                description = "30 minutes workout",
                reward = Reward(gold = 100),
                penalty = Penalty(),
                deadline = null,
                date = "2026-02-19",
                difficulty = "mortal"
            ),
        )
    }
}