package com.sae.wavetime.engine.timer

import android.content.Context
import android.util.Log
import com.sae.wavetime.analytics.AnalyticsTracker
import com.sae.wavetime.data.repository.InventoryRepository
import com.sae.wavetime.data.repository.TaskRepository
import com.sae.wavetime.domain.usecase.CompleteTaskUseCase
import com.sae.wavetime.engine.alarm.TaskTimerAlarmScheduler
import com.sae.wavetime.engine.service.TaskTimerService
import com.sae.wavetime.local.DatabaseProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object TaskTimerRecoveryManager {

    private const val TAG = "TaskTimerRecovery"

    suspend fun recover(context: Context) {
        withContext(Dispatchers.IO) {
            val appContext = context.applicationContext
            val db = DatabaseProvider.getDatabase(appContext)

            val taskRepo = TaskRepository(
                taskDao = db.taskDao(),
                templateDao = db.taskTemplateDao()
            )

            val inventoryRepo = InventoryRepository(
                inventoryDao = db.inventoryDao()
            )

            val completeTaskUseCase = CompleteTaskUseCase(
                taskRepo = taskRepo,
                inventoryRepo = inventoryRepo,
                database = db,
                analyticsLogger = AnalyticsTracker(appContext)
            )

            val runningTask = taskRepo.getRunningTimerTask()

            if (runningTask == null) {
                Log.d(TAG, "No running timer task")
                return@withContext
            }

            val finishAt = runningTask.finishAt

            if (finishAt == null || finishAt <= 0L) {
                Log.e(TAG, "Running task has invalid finishAt taskId=${runningTask.id}")
                return@withContext
            }

            val now = System.currentTimeMillis()

            if (finishAt <= now) {
                Log.d(TAG, "Timer already expired, complete taskId=${runningTask.id}")
                completeTaskUseCase.execute(runningTask.id)
                return@withContext
            }

            Log.d(TAG, "Recover timer service taskId=${runningTask.id}")

            TaskTimerAlarmScheduler.schedule(
                context = appContext,
                taskId = runningTask.id,
                finishAt = finishAt
            )

            TaskTimerService.start(
                context = appContext,
                taskId = runningTask.id,
                finishAt = finishAt
            )
        }
    }
}