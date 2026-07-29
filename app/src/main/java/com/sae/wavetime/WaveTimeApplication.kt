package com.sae.wavetime

import android.app.Application
import com.sae.wavetime.engine.block.BlockReactivationReconciler
import com.sae.wavetime.engine.block.BlockReactivationScheduler
import com.sae.wavetime.local.AppDatabase
import com.sae.wavetime.local.DatabaseProvider

class WaveTimeApplication : Application() {
    val database: AppDatabase by lazy {
        DatabaseProvider.getDatabase(this)
    }

    private val blockReactivationScheduler:
            BlockReactivationScheduler by lazy {

        BlockReactivationScheduler(
            context = this
        )
    }

    val blockReactivationReconciler:
            BlockReactivationReconciler by lazy {

        BlockReactivationReconciler(
            database = database,
            scheduler = blockReactivationScheduler
        )
    }
}