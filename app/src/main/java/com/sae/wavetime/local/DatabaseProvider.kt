package com.sae.wavetime.local

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.sae.wavetime.domain.model.BlockOfItem
import com.sae.wavetime.domain.model.KeyInfoPopulated
import com.sae.wavetime.domain.model.Penalty
import com.sae.wavetime.domain.model.Reward
import com.sae.wavetime.data.model.entity.BlockEntity
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
        val blockDao = db.blockDao()
        val itemDao = db.itemDao()
        val inventoryDao = db.inventoryDao()
        val taskDao = db.taskDao()

        // ===== 1. Fake Block =====
        val block1 = BlockEntity(
            id = "block_1",
            appName = "YouTube",
            packageName = "com.google.android.youtube",
            blockType = "time",
            penaltyMinutes = 15,
            isActive = true
        )

        val block2 = BlockEntity(
            id = "block_2",
            appName = "Facebook",
            packageName = "com.facebook.katana",
            blockType = "permanent",
            penaltyMinutes = 60,
            isActive = true
        )

        val block3 = BlockEntity(
            id = "block_3",
            appName = "TikTok",
            packageName = "com.zhiliaoapp.musically",
            blockType = "time",
            penaltyMinutes = 30,
            isActive = false
        )

        blockDao.insert(block1)
        blockDao.insert(block2)
        blockDao.insert(block3)

        // ===== 2. Fake Item =====
        val item1 = ItemEntity(
            id = "item_1",
            name = "Basic YouTube Key",
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
            description = "Unlock YouTube for a short time",
            icon = "icon_key_white"
        )

        val item2 = ItemEntity(
            id = "item_2",
            name = "Master Facebook Key",
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
            description = "Master key for Facebook block",
            icon = "icon_key_purple"
        )

        val item3 = ItemEntity(
            id = "item_3",
            name = "TikTok Trial Key",
            tier = "green",
            rank = 2,
            category = "key",
            keyInfo = KeyInfoPopulated(
                blockId = BlockOfItem(
                    id = "block_3",
                    appName = "TikTok",
                    packageName = "com.zhiliaoapp.musically",
                    blockType = "time"
                ),
                isMaster = false
            ),
            description = "Unlock TikTok temporarily",
            icon = "icon_key_green"
        )

        itemDao.insert(item1)
        itemDao.insert(item2)
        itemDao.insert(item3)

        // ===== 3. Fake Inventory =====
        val inventory1 = InventoryEntity(
            id = "inventory_1",
            itemId = "item_1",
            quantity = 3
        )

        val inventory2 = InventoryEntity(
            id = "inventory_2",
            itemId = "item_2",
            quantity = 1
        )

        val inventory3 = InventoryEntity(
            id = "inventory_3",
            itemId = "item_3",
            quantity = 5
        )

        inventoryDao.insert(inventory1)
        inventoryDao.insert(inventory2)
        inventoryDao.insert(inventory3)

        // ===== 4. Fake Task =====
        val task1 = TaskEntity(
            id = "task_1",
            name = "Study Kotlin for 30 minutes",
            description = "Read and practice Room Database basics",
            reward = Reward(
                // sửa lại theo data class Reward của bạn
                exp = 50,
                gold = 20
            ),
            penalty = Penalty(
                // sửa lại theo data class Penalty của bạn
                exp = -10,
                gold = -5
            ),
            deadline = "2026-03-29T21:00:00",
            date = "2026-03-28",
            difficulty = "easy",
            status = "pending"
        )

        val task2 = TaskEntity(
            id = "task_2",
            name = "Do 20 push-ups",
            description = "Complete before evening",
            reward = Reward(
                exp = 80,
                gold = 30
            ),
            penalty = Penalty(
                exp = -15,
                gold = -10
            ),
            deadline = "2026-03-29T18:00:00",
            date = "2026-03-28",
            difficulty = "medium",
            status = "pending"
        )

        val task3 = TaskEntity(
            id = "task_3",
            name = "No YouTube for 2 hours",
            description = "Stay focused and avoid distraction",
            reward = Reward(
                exp = 120,
                gold = 50
            ),
            penalty = Penalty(
                exp = -25,
                gold = -15
            ),
            deadline = "2026-03-29T23:00:00",
            date = "2026-03-28",
            difficulty = "hard",
            status = "pending"
        )

        taskDao.insert(task1)
        taskDao.insert(task2)
        taskDao.insert(task3)
    }
}