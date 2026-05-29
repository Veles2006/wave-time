package com.sae.wavetime.engine.service

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import com.sae.wavetime.data.repository.InventoryRepository
import com.sae.wavetime.data.repository.TaskRepository
import com.sae.wavetime.domain.usecase.CompleteTaskUseCase
import com.sae.wavetime.engine.alarm.TaskTimerAlarmScheduler
import com.sae.wavetime.engine.notification.TaskTimerNotification
import com.sae.wavetime.local.DatabaseProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskTimerService : Service() {

    private val handler = Handler(Looper.getMainLooper())

    private var taskId: String? = null
    private var finishAt: Long = 0L

    private val TAG = "TaskTimerService"

    private val timerRunnable = object : Runnable {
        override fun run() {
            val remainingMillis = finishAt - System.currentTimeMillis()

            if (remainingMillis <= 0L) {
                taskId?.let {
                    completeTaskFromService(it)
                }

                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
                return
            }

            updateNotification(remainingMillis)

            handler.postDelayed(this, 1000L)
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
        TaskTimerNotification.createChannel(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand called")
        taskId = intent?.getStringExtra(EXTRA_TASK_ID)
        finishAt = intent?.getLongExtra(EXTRA_FINISH_AT, 0L) ?: 0L

        Log.d(TAG, "taskId=$taskId finishAt=$finishAt")

        if (taskId == null || finishAt <= 0L) {
            stopSelf()
            return START_NOT_STICKY
        }

        TaskTimerAlarmScheduler.schedule(
            context = this,
            taskId = taskId!!,
            finishAt = finishAt
        )

        val remainingMillis = finishAt - System.currentTimeMillis()

        startForeground(
            TaskTimerNotification.NOTIFICATION_ID,
            TaskTimerNotification.build(
                context = this,
                remainingMillis = remainingMillis
            )
        )

        handler.removeCallbacks(timerRunnable)
        handler.post(timerRunnable)

        return START_STICKY
    }

    private fun updateNotification(remainingMillis: Long) {
        val notification = TaskTimerNotification.build(
            context = this,
            remainingMillis = remainingMillis
        )

        val manager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        manager.notify(TaskTimerNotification.NOTIFICATION_ID, notification)
    }

    private fun completeTaskFromService(taskId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = DatabaseProvider.getDatabase(applicationContext)

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

                completeTaskUseCase.execute(taskId)

                Log.d(TAG, "completeTaskFromService success taskId=$taskId")
            } catch (e: Exception) {
                Log.e(TAG, "completeTaskFromService failed", e)
            }
        }
    }

    override fun onDestroy() {
        handler.removeCallbacks(timerRunnable)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        private const val EXTRA_TASK_ID = "extra_task_id"
        private const val EXTRA_FINISH_AT = "extra_finish_at"

        fun start(
            context: Context,
            taskId: String,
            finishAt: Long
        ) {
            val intent = Intent(context, TaskTimerService::class.java).apply {
                putExtra(EXTRA_TASK_ID, taskId)
                putExtra(EXTRA_FINISH_AT, finishAt)
            }

            context.startForegroundService(intent)
        }
    }
}