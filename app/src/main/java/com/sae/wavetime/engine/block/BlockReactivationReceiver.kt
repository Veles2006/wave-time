package com.sae.wavetime.engine.block

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.sae.wavetime.WaveTimeApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import okhttp3.Dispatcher

class BlockReactivationReceiver : BroadcastReceiver() {

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {
        if (
            intent.action !=
            BlockReactivationScheduler.ACTION_REACTIVATE_BLOCK
        ) {
            return
        }

        val blockId = intent.getStringExtra(
            BlockReactivationScheduler.EXTRA_BLOCK_ID
        ) ?: return

        val pendingResult = goAsync()

        CoroutineScope(
            SupervisorJob() + Dispatchers.IO
        ).launch {
            try {
                val application =
                    context.applicationContext as WaveTimeApplication

                application.database
                    .blockDao()
                    .reactivateBlockIfDue(
                        blockId = blockId,
                        now = System.currentTimeMillis()
                    )
            } finally {
                pendingResult.finish()
            }
        }
    }
}