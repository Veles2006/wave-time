package com.sae.wavetime.engine.service

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.sae.wavetime.data.repository.BlockRepository
import com.sae.wavetime.data.resolver.AppIconResolver
import com.sae.wavetime.data.resolver.InstalledAppResolver
import com.sae.wavetime.domain.model.Block
import com.sae.wavetime.local.DatabaseProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class FocusAccessibilityService : AccessibilityService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    @Volatile
    private var blocks: List<Block> = emptyList()

    override fun onServiceConnected() {
        super.onServiceConnected()


        val db = DatabaseProvider.getDatabase(applicationContext)

        val blockRepo = BlockRepository(
            db.blockDao(),
            AppIconResolver(applicationContext),
            InstalledAppResolver(applicationContext)
        )

        serviceScope.launch {
            blockRepo.observeActiveBlocks()
                .catch { e ->
                    blocks = emptyList()
                }
                .collect { newBlocks ->
                    blocks = newBlocks
                }
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event ?: return

        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return

        val currentPackageName = event.packageName?.toString() ?: return

        if (currentPackageName == applicationContext.packageName) return

        val block = blocks.find {
            it.packageName == currentPackageName
        } ?: return

        val now = System.currentTimeMillis()

        if (now < block.unlockUntil) {
            return
        }

        blockApp(block)
    }

    private fun blockApp(block: Block) {
        performGlobalAction(GLOBAL_ACTION_HOME)
    }

    override fun onInterrupt() {
        // Không cần làm gì tạm thời
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }
}