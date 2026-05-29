package com.sae.wavetime.engine.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.sae.wavetime.data.repository.InventoryRepository
import com.sae.wavetime.data.repository.TaskRepository
import com.sae.wavetime.domain.usecase.CompleteTaskUseCase
import com.sae.wavetime.local.DatabaseProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskTimerReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()

        val taskId = intent.getStringExtra(EXTRA_TASK_ID)
        Log.d("TaskTimerReceiver", "onReceive taskId=$taskId")
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
                    database = db
                )
                Log.d("TaskTimerReceiver", "completeTask executed")

                completeTaskUseCase.execute(taskId)
            } finally {
                pendingResult.finish()
            }
        }
    }

    companion object {
        const val EXTRA_TASK_ID = "extra_task_id"
    }
}