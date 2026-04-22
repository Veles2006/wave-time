package com.sae.wavetime.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.sae.wavetime.data.model.entity.InventoryEntity
import com.sae.wavetime.data.model.relation.InventoryWithItem

@Dao
interface InventoryDao {
    @Transaction
    @Query("SELECT * FROM inventory")
    suspend fun getInventoryWithItem(): List<InventoryWithItem>

    @Query("SELECT * FROM inventory WHERE itemId = :itemId LIMIT 1")
    suspend fun getByItemId(itemId: String): InventoryEntity?

    @Query("SELECT * FROM inventory")
    suspend fun getAll(): List<InventoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(inventory: InventoryEntity)

    @Query("""
        UPDATE inventory
        SET quantity = quantity + :amount
        WHERE itemId = :itemId
    """)
    suspend fun addQuantity(
        itemId: String,
        amount: Int
    )

    @Query("""
        UPDATE inventory
        SET quantity = quantity - :amount
        WHERE itemId = :itemId AND quantity >= :amount
    """)
    suspend fun subtractQuantity(
        itemId: String,
        amount: Int
    )

    @Delete
    suspend fun delete(inventory: InventoryEntity)
}