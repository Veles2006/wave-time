package com.sae.wavetime.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sae.wavetime.domain.model.Penalty
import com.sae.wavetime.domain.model.Reward
import com.sae.wavetime.data.model.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    // Get method
    @Query("SELECT * FROM tasks WHERE isDeleted = 0")
    fun getAll(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :id AND isDeleted = 0 LIMIT 1")
    suspend fun getById(id: String): TaskEntity?

    @Query("SELECT * FROM tasks WHERE id = :id AND isDeleted = 0")
    fun getByIdFlow(id: String): Flow<TaskEntity?>

    @Query("""
        SELECT * FROM tasks
        WHERE templateId = :templateId
        AND date = :date
        LIMIT 1
    """)
    suspend fun getTaskByTemplateAndDate(
        templateId: String,
        date: String
    ): TaskEntity?

    // Create method
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: TaskEntity)

    // Update method
    @Query("""
        UPDATE tasks
        SET name = :name,
            description = :description,
            status = :status,
            type = :type,
            completeMode = :completeMode,
            startedAt = :startedAt,
            finishAt = :finishAt,
            reward = :reward,
            penalty = :penalty,
            requiredDurationMinutes = :requiredDurationMinutes,
            deadline = :deadline,
            date = :date,
            difficulty = :difficulty
        WHERE id = :taskId AND isDeleted = 0
    """)
    suspend fun updateFull(
        taskId: String,
        name: String,
        description: String?,
        status: String,
        type: String,
        completeMode: String,
        startedAt: Long?,
        finishAt: Long?,
        reward: Reward,
        penalty: Penalty,
        requiredDurationMinutes: Int?,
        deadline: String?,
        date: String?,
        difficulty: String
    )

    @Query("""
        UPDATE tasks
        SET status = :status,
            lastCompletedAt = :lastCompletedAt,
            finishAt = null
        WHERE id = :taskId AND isDeleted = 0
    """)
    suspend fun changeStatus(
        taskId: String,
        status: String,
        lastCompletedAt: Long?
    )

    // Delete method
    @Query("DELETE FROM tasks")
    suspend fun clearAll()
    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun hardDeleteById(taskId: String)

    @Query("UPDATE tasks SET isDeleted = 1, finishAt = null WHERE id = :taskId")
    suspend fun softDelete(taskId: String)
}