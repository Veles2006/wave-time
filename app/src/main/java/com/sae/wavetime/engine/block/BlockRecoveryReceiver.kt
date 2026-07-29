package com.sae.wavetime.engine.block

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.sae.wavetime.WaveTimeApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class BlockRecoveryReceiver : BroadcastReceiver() {
    override fun onReceive(
        context: Context,
        intent: Intent
    ) {
        val supportedAction =
            intent.action == Intent.ACTION_BOOT_COMPLETED ||
                    intent.action == Intent.ACTION_MY_PACKAGE_REPLACED

        if (!supportedAction) {
            return
        }

        val pendingResult = goAsync()

        CoroutineScope(
            SupervisorJob() + Dispatchers.IO
        ).launch {
            try {
                val application =
                    context.applicationContext as WaveTimeApplication

                application.blockReactivationReconciler
                    .reconcile()
            } finally {
                pendingResult.finish()
            }
        }
    }
}