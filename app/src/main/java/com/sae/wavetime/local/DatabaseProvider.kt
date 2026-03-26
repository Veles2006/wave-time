package com.sae.wavetime.local

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.sae.wavetime.data.model.entity.BlockEntity
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
    }
}