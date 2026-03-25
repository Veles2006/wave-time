package com.sae.wavetime.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sae.wavetime.data.dao.BlockDao
import com.sae.wavetime.data.dao.InventoryDao
import com.sae.wavetime.data.dao.ItemDao
import com.sae.wavetime.data.dao.TaskDao
import com.sae.wavetime.data.model.entity.BlockEntity
import com.sae.wavetime.data.model.entity.InventoryEntity
import com.sae.wavetime.data.model.entity.ItemEntity
import com.sae.wavetime.data.model.entity.TaskEntity
import com.sae.wavetime.data.room.Converters

@Database(
    entities = [
        TaskEntity::class,
        ItemEntity::class,
        InventoryEntity::class,
        BlockEntity::class
    ],
    version = 1
)

@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun itemDao(): ItemDao
    abstract fun inventoryDao(): InventoryDao
    abstract fun blockDao(): BlockDao
}