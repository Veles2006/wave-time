package com.sae.wavetime.local

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.sae.wavetime.data.model.api.BlockOfItem
import com.sae.wavetime.data.model.api.KeyInfoPopulated
import com.sae.wavetime.data.model.api.Penalty
import com.sae.wavetime.data.model.api.Reward
import com.sae.wavetime.data.model.entity.InventoryEntity
import com.sae.wavetime.data.model.entity.ItemEntity
import com.sae.wavetime.data.model.entity.TaskEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object DatabaseProvider {

    private var instant: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {

        if (instant == null) {
            instant = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "wave_time_db"
            )
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)

                    CoroutineScope(Dispatchers.IO).launch {
                        seedDatabase(getDatabase(context))
                    }

                    Log.d("DB", "onCreate called")
                }
            })
            .build()
        }



        return instant!!
    }


    suspend fun seedDatabase(db: AppDatabase) {

        val itemDao = db.itemDao()
        val inventoryDao = db.inventoryDao()

        // ===== 1. Fake Item =====
        val item1 = ItemEntity(
            id = "item_1",
            name = "Basic Key",
            tier = "white",
            rank = 1,
            category = "key",
            keyInfo = KeyInfoPopulated(
                blockId = BlockOfItem(
                    id = "block_1",
                    appName = "YouTube",
                    packageName = "com.google.android.youtube",
                    blockType = "time"
                ),
                isMaster = false
            ),
            description = "Unlock app for short time",
            icon = "icon_key_white"
        )

        val item2 = ItemEntity(
            id = "item_2",
            name = "Master Key",
            tier = "purple",
            rank = 5,
            category = "key",
            keyInfo = KeyInfoPopulated(
                blockId = BlockOfItem(
                    id = "block_2",
                    appName = "Facebook",
                    packageName = "com.facebook.katana",
                    blockType = "permanent"
                ),
                isMaster = true
            ),
            description = "Unlock everything",
            icon = "icon_key_purple"
        )

        itemDao.insert(item1)
        itemDao.insert(item2)

        // ===== 2. Fake Inventory =====
        val inventory1 = InventoryEntity(
            id = "inv_1",
            itemId = "item_1",
            quantity = 3
        )

        val inventory2 = InventoryEntity(
            id = "inv_2",
            itemId = "item_2",
            quantity = 1
        )

        inventoryDao.insert(inventory1)
        inventoryDao.insert(inventory2)
    }
}