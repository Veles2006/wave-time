package com.sae.wavetime.engine.service

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import com.sae.wavetime.analytics.AnalyticsTracker
import com.sae.wavetime.data.repository.InventoryRepository
import com.sae.wavetime.data.repository.TaskRepository
import com.sae.wavetime.domain.usecase.CompleteTaskUseCase
import com.sae.wavetime.engine.alarm.TaskTimerAlarmScheduler
import com.sae.wavetime.engine.event.TaskEvent
import com.sae.wavetime.engine.event.TaskEventBus
import com.sae.wavetime.engine.notification.TaskCompletionNotifier
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
        Log.d(TAG, "onStartCommand called intent=$intent")

        if (intent == null) {
            recoverRunningTimer()
            return START_STICKY
        }

        taskId = intent.getStringExtra(EXTRA_TASK_ID)
        finishAt = intent.getLongExtra(EXTRA_FINISH_AT, 0L)

        if (taskId == null || finishAt <= 0L) {
            stopSelf()
            return START_NOT_STICKY
        }

        startTimer(taskId!!, finishAt)

        return START_REDELIVER_INTENT
    }

    private fun startTimer(
        taskId: String,
        finishAt: Long
    ) {
        this.taskId = taskId
        this.finishAt = finishAt

        TaskTimerAlarmScheduler.schedule(
            context = this,
            taskId = taskId,
            finishAt = finishAt
        )

        val remainingMillis = finishAt - System.currentTimeMillis()

        startForeground(
            TaskTimerNotification.NOTIFICATION_ID,
            TaskTimerNotification.build(
                context = this,
                remainingMillis = remainingMillis.coerceAtLeast(0L)
            )
        )

        handler.removeCallbacks(timerRunnable)
        handler.post(timerRunnable)
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
                    database = db,
                    analyticsLogger = AnalyticsTracker(applicationContext)
                )

                val task = taskRepo.getTaskById(taskId)

                if (task == null) {
                    Log.d(TAG, "Task not found, skip event taskId=$taskId")
                    return@launch
                }

                val wasCompletedNow = completeTaskUseCase.execute(taskId)

                if (!wasCompletedNow) {
                    Log.d(
                        TAG,
                        "Task was already completed taskId=$taskId"
                    )
                    return@launch
                }

                TaskTimerAlarmScheduler.cancel(
                    context = applicationContext,
                    taskId = taskId
                )

                TaskCompletionNotifier.show(
                    context = applicationContext,
                    taskId = task.id,
                    taskName = task.name
                )

                TaskEventBus.send(
                    TaskEvent.TaskCompletedByTimer(
                        taskId = task.id,
                        taskName = task.name
                    )
                )

                Log.d(TAG, "completeTaskFromService success taskId=$taskId")
            } catch (e: Exception) {
                Log.e(TAG, "completeTaskFromService failed", e)
            }
        }
    }

    private fun recoverRunningTimer() {
        CoroutineScope(Dispatchers.IO).launch {
            val db = DatabaseProvider.getDatabase(applicationContext)

            val taskRepo = TaskRepository(
                taskDao = db.taskDao(),
                templateDao = db.taskTemplateDao()
            )

            val runningTask = taskRepo.getRunningTimerTask()

            if (runningTask == null || runningTask.finishAt == null) {
                stopSelf()
                return@launch
            }

            val now = System.currentTimeMillis()

            if (runningTask.finishAt <= now) {
                completeTaskFromService(runningTask.id)
                stopSelf()
                return@launch
            }

            handler.post {
                startTimer(
                    taskId = runningTask.id,
                    finishAt = runningTask.finishAt
                )
            }
        }
    }

    private fun stopTimer() {
        handler.removeCallbacks(timerRunnable)

        taskId = null
        finishAt = 0L

        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        handler.removeCallbacks(timerRunnable)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        private const val EXTRA_TASK_ID = "extra_task_id"
        private const val EXTRA_FINISH_AT = "extra_finish_at"

        private const val ACTION_START_TIMER =
            "com.sae.wavetime.action.START_TIMER"

        private const val ACTION_STOP_TIMER =
            "com.sae.wavetime.action.STOP_TIMER"

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

        fun stop(context: Context) {
            val intent = Intent(context, TaskTimerService::class.java).apply {
                action = ACTION_STOP_TIMER
            }

            context.startService(intent)
        }
    }
}