package com.sae.wavetime.engine.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.sae.wavetime.analytics.AnalyticsTracker
import com.sae.wavetime.data.repository.InventoryRepository
import com.sae.wavetime.data.repository.TaskRepository
import com.sae.wavetime.domain.usecase.CompleteTaskUseCase
import com.sae.wavetime.engine.event.TaskEvent
import com.sae.wavetime.engine.event.TaskEventBus
import com.sae.wavetime.engine.notification.TaskCompletionNotifier
import com.sae.wavetime.local.DatabaseProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskTimerReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()

        val taskId = intent.getStringExtra(EXTRA_TASK_ID)
        Log.d(TAG, "onReceive taskId=$taskId")
        if (taskId == null) {
            pendingResult.finish()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
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
                    analyticsLogger = AnalyticsTracker(context)
                )
                val task = taskRepo.getTaskById(taskId)

                if (task == null) {
                    Log.d(
                        TAG,
                        "Task not found taskId=$taskId"
                    )
                    return@launch
                }

                val wasCompletedNow = completeTaskUseCase.execute(taskId)

                if (!wasCompletedNow) {
                    Log.d(
                        TAG,
                        "Task already completed taskId=$taskId"
                    )
                    return@launch
                }

                TaskCompletionNotifier.show(
                    context = appContext,
                    taskId = task.id,
                    taskName = task.name
                )

                TaskEventBus.send(
                    TaskEvent.TaskCompletedByTimer(
                        taskId = task.id,
                        taskName = task.name
                    )
                )

                Log.d(
                    TAG,
                    "Task completed by receiver taskId=$taskId"
                )
            } catch (e: Exception) {
                Log.e(
                    TAG,
                    "Timer completion failed taskId=$taskId",
                    e
                )
            } finally {
                pendingResult.finish()
            }
        }
    }

    companion object {
        const val EXTRA_TASK_ID = "extra_task_id"
        const val TAG = "TaskTimerReceiver"
    }
}