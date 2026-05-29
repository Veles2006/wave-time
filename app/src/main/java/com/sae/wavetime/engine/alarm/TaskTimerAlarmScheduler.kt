package com.sae.wavetime.engine.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.sae.wavetime.engine.receiver.TaskTimerReceiver

object TaskTimerAlarmScheduler {

    fun schedule(
        context: Context,
        taskId: String,
        finishAt: Long
    ) {
        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val pendingIntent = createPendingIntent(
            context = context,
            taskId = taskId
        )
        Log.d("TaskTimerAlarmScheduler", taskId)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    finishAt,
                    pendingIntent
                )
                return
            }
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            finishAt,
            pendingIntent
        )
    }

    fun cancel(
        context: Context,
        taskId: String
    ) {
        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val pendingIntent = createPendingIntent(
            context = context,
            taskId = taskId
        )

        alarmManager.cancel(pendingIntent)
    }

    private fun createPendingIntent(
        context: Context,
        taskId: String
    ): PendingIntent {
        val intent = Intent(context, TaskTimerReceiver::class.java).apply {
            putExtra(TaskTimerReceiver.EXTRA_TASK_ID, taskId)
        }

        return PendingIntent.getBroadcast(
            context,
            taskId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}