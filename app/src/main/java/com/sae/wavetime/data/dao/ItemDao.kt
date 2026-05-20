package com.sae.wavetime.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sae.wavetime.data.model.entity.ItemEntity

@Dao
interface ItemDao {
    // Get method
    @Query("""
        SELECT items.*
        FROM items
        LEFT JOIN blocks ON items.blockId = blocks.id
        WHERE items.category != 'key'
           OR blocks.isDeleted = 0
    """)
    suspend fun getVisibleItems(): List<ItemEntity>

    @Query("SELECT * FROM items WHERE id = :id LIMIT 1")
    suspend fun getItemById(id: String): ItemEntity?

    // Create method

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ItemEntity)

    @Delete
    suspend fun delete(item: ItemEntity)
}