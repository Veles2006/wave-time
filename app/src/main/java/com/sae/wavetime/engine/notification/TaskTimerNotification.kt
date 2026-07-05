package com.sae.wavetime.engine.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.sae.wavetime.R

object TaskTimerNotification {

    const val CHANNEL_ID = "task_timer_channel"
    const val NOTIFICATION_ID = 1001

    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Task Timer",
                NotificationManager.IMPORTANCE_LOW
            )

            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            manager.createNotificationChannel(channel)
        }
    }

    fun build(
        context: Context,
        taskName: String = context.getString(R.string.running_timer_task_title),
        remainingMillis: Long
    ): Notification {
        val remainingText = formatRemainingTime(remainingMillis)

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_clock_rotate_left_solid_full)
            .setContentTitle(taskName)
            .setContentText(context.getString(R.string.remaining_time_format, remainingText))
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun formatRemainingTime(millis: Long): String {
        val totalSeconds = millis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60

        return "%02d:%02d".format(minutes, seconds)
    }
}