package com.sae.wavetime.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sae.wavetime.data.model.entity.TaskTemplateEntity

@Dao
interface TaskTemplateDao {

    @Query("""
        SELECT * FROM task_templates
        WHERE repeatType = 'daily'
        AND isActive = 1
    """)
    suspend fun getActiveDailyTemplates(): List<TaskTemplateEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(template: TaskTemplateEntity): Long
}