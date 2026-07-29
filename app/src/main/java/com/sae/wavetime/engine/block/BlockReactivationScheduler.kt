package com.sae.wavetime.engine.block

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.net.toUri

class BlockReactivationScheduler(
    private val context: Context
) {
    private val appContext = context.applicationContext

    fun schedule(
        blockId: String,
        reactivateAt: Long
    ) {
        val alarmManager =
            appContext.getSystemService(
                Context.ALARM_SERVICE
            ) as AlarmManager

        val pendingIntent = createPendingIntent(blockId)

        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            !alarmManager.canScheduleExactAlarms()
        ) {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                reactivateAt,
                pendingIntent
            )
            return
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            reactivateAt,
            pendingIntent
        )
    }

    fun cancel(blockId: String) {
        val alarmManager =
            context.getSystemService(
                Context.ALARM_SERVICE
            ) as AlarmManager

        alarmManager.cancel(
            createPendingIntent(blockId)
        )
    }

    private fun createPendingIntent(
        blockId: String
    ): PendingIntent {
        val intent = Intent(
            appContext,
            BlockReactivationReceiver::class.java
        ).apply {
            action = ACTION_REACTIVATE_BLOCK

            data = "wavetime://block/reactivate/$blockId".toUri()

            putExtra(EXTRA_BLOCK_ID, blockId)
        }

        return PendingIntent.getBroadcast(
            appContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )
    }

    companion object {
        const val ACTION_REACTIVATE_BLOCK =
            "com.sae.wavetime.action.REACTIVATE_BLOCK"

        const val EXTRA_BLOCK_ID =
            "extra_block_id"
    }
}