package com.sae.wavetime.engine.notification

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.sae.wavetime.R

object TaskCompletionNotifier {

    private const val TAG = "TaskCompletionNotifier"

    const val CHANNEL_ID = "task_completion_alarm_v1"

    private const val NOTIFICATION_ID = 2001

    private val vibrationPattern = longArrayOf(
        0,
        250L,
        150L,
        250L,
        150L,
        500L
    )

    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }

        val notificationManager =
            context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

        val alarmSoundUri = Uri.parse(
            "android.resource://${context.packageName}/${R.raw.pomodoro_chime_3s}"
        )

        val alarmAudioAttributes =
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(
                    AudioAttributes.CONTENT_TYPE_SONIFICATION
                )
                .build()

        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(
                R.string.task_completion_channel_name,
            ),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = context.getString(
                R.string.task_completion_channel_description
            )

            setSound(
                alarmSoundUri,
                alarmAudioAttributes
            )

            enableVibration(true)
            vibrationPattern = TaskCompletionNotifier.vibrationPattern

            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }

        notificationManager.createNotificationChannel(channel)
    }

    fun show(
        context: Context,
        taskId: String,
        taskName: String
    ) {
        val appContext = context.applicationContext

        createChannel(appContext)

        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                appContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.w(
                TAG,
                "POST_NOTIFICATIONS permission is not granted"
            )
            return
        }

        val contentIntent = createOpenAppPendingIntent(
            context = appContext,
            taskId = taskId
        )

        val notification = NotificationCompat.Builder(
            appContext,
            CHANNEL_ID
        )
            .setSmallIcon(
                R.drawable.ic_clock_rotate_left_solid_full
            )
            .setContentTitle(
                appContext.getString(
                    R.string.task_completed_notification_title
                )
            )
            .setContentText(
                appContext.getString(
                    R.string.task_completed_notification_message,
                    taskName
                )
            )
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
            .setOnlyAlertOnce(false)
            .setContentIntent(contentIntent)
            .build()

        NotificationManagerCompat
            .from(appContext)
            .notify(
                NOTIFICATION_ID,
                notification
            )

        Log.d(
            TAG,
            "Completion notification shown taskId=$taskId"
        )
    }

    private fun createOpenAppPendingIntent(
        context: Context,
        taskId: String
    ): PendingIntent? {
        val launchIntent =
            context.packageManager
                .getLaunchIntentForPackage(context.packageName)
                ?.apply {
                    flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TOP

                    putExtra(EXTRA_TASK_ID, taskId)
                    putExtra(EXTRA_OPEN_SOURCE, SOURCE_TIMER_COMPLETION)
                }
                ?: return null

        return PendingIntent.getActivity(
            context,
            OPEN_APP_REQUEST_CODE,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )
    }

    private const val OPEN_APP_REQUEST_CODE = 2001

    const val EXTRA_TASK_ID = "completion_task_id"
    const val EXTRA_OPEN_SOURCE = "completion_open_source"

    private const val SOURCE_TIMER_COMPLETION =
        "timer_completion"
}