package com.sae.wavetime.engine.service

import android.accessibilityservice.AccessibilityService
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
import android.widget.Toast
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

class FocusAccessibilityService : AccessibilityService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    @Volatile
    private var blocks: List<Block> = emptyList()
    @Volatile
    private var currentPackageName: String? = null

    private var blockJob: Job? = null
    private var unlockWatcherJob: Job? = null

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

                    launch(Dispatchers.Main) {
                        checkCurrentApp()
                    }
                }
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event ?: return

        if (
            event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED &&
            event.eventType != AccessibilityEvent.TYPE_WINDOWS_CHANGED
        ) {
            return
        }

        val packageName = event.packageName?.toString() ?: return

        if (packageName == applicationContext.packageName) return

        currentPackageName = packageName

        checkCurrentApp()
    }

    private fun checkCurrentApp() {
        val packageName = currentPackageName ?: return

        if (packageName == applicationContext.packageName) return

        val block = blocks.find {
            it.packageName == packageName
        } ?: return

        val now = System.currentTimeMillis()

        if (now < block.unlockUntil) {
            scheduleBlockWhenUnlockExpires(block)
            return
        }

        blockApp(block)
    }

    private fun scheduleBlockWhenUnlockExpires(block: Block) {
        unlockWatcherJob?.cancel()

        val delayMs = (block.unlockUntil - System.currentTimeMillis())
            .coerceAtLeast(0L)

        unlockWatcherJob = serviceScope.launch(Dispatchers.Main) {
            delay(delayMs)

            if (currentPackageName == block.packageName) {
                checkCurrentApp()
            }
        }
    }

    private fun blockApp(block: Block) {
        if (blockJob?.isActive == true) return

        blockJob = serviceScope.launch(Dispatchers.Main) {
            Toast.makeText(
                applicationContext,
                "The world",
                Toast.LENGTH_SHORT
            ).show()

            delay(800)

            performGlobalAction(GLOBAL_ACTION_HOME)
        }
    }

    override fun onInterrupt() {
        // Không cần làm gì tạm thời
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }
}