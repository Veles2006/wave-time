package com.sae.wavetime.local

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.sae.wavetime.data.model.api.Penalty
import com.sae.wavetime.data.model.api.Reward
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
        val taskDao = db.taskDao()

        taskDao.insert(
            TaskEntity(
                id = "123",
                name = "Test",
                description = "abc",
                reward = Reward(gold = 123),
                penalty = Penalty(gold = 123),
                deadline = "abc",
                date = "abc",
                difficulty = "motal",
            )
        )

        taskDao.insert(
            TaskEntity(
                id = "1232",
                name = "Test 2",
                description = "The world",
                reward = Reward(gold = 123),
                penalty = Penalty(gold = 123),
                deadline = "abc",
                date = "abc",
                difficulty = "motal",
            )
        )
    }
}