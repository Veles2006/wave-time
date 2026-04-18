package com.sae.wavetime.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sae.wavetime.data.model.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    // Get method
    @Query("SELECT * FROM tasks WHERE isDeleted = 0")
    fun getAll(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :id AND isDeleted = 0")
    suspend fun getById(id: String): TaskEntity?

    @Query("SELECT * FROM tasks WHERE id = :id AND isDeleted = 0")
    fun getByIdFlow(id: String): Flow<TaskEntity?>

    // Create method
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: TaskEntity)

    // Delete method
    @Query("DELETE FROM tasks")
    suspend fun clearAll()
    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun hardDeleteById(taskId: String)

    @Query("UPDATE tasks SET isDeleted = 1 WHERE id = :taskId")
    suspend fun softDelete(taskId: String)
}