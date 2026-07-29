package com.sae.wavetime.engine.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.sae.wavetime.data.repository.BlockRepository
import com.sae.wavetime.data.resolver.AppIconResolver
import com.sae.wavetime.data.resolver.InstalledAppResolver
import com.sae.wavetime.domain.model.Block
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import com.sae.wavetime.BlockedActivity
import com.sae.wavetime.WaveTimeApplication
import com.sae.wavetime.analytics.AnalyticsLogger
import com.sae.wavetime.analytics.AnalyticsTracker
import com.sae.wavetime.domain.block.isReactivationDue
import com.sae.wavetime.domain.block.shouldBeActive
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class FocusAccessibilityService : AccessibilityService() {

    companion object {
        private const val TAG = "FocusService"
    }
    private lateinit var analyticsLogger: AnalyticsLogger
    private var launcherPackageName: String? = null

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    @Volatile
    private var blocks: List<Block> = emptyList()
    @Volatile
    private var currentPackageName: String? = null
    private lateinit var blockRepository: BlockRepository
    private val reactivationInProgress = mutableSetOf<String>()
    private var blockJob: Job? = null
    private var unlockWatcherJob: Job? = null

    private var watchedBlockId: String? = null
    private var watchedUnlockUntil: Long = 0L

    override fun onServiceConnected() {
        super.onServiceConnected()

        val application =
            applicationContext as WaveTimeApplication

        val db = application.database

        blockRepository = BlockRepository(
            db.blockDao(),
            AppIconResolver(applicationContext),
            InstalledAppResolver(applicationContext)
        )

        analyticsLogger = AnalyticsTracker(applicationContext)

        launcherPackageName = getDefaultLauncherPackage()
        Log.d(TAG, "Default launcher = $launcherPackageName")

        serviceScope.launch {
            try {
                application.blockReactivationReconciler
                    .reconcile()
            } catch (e: Exception) {
                Log.e(
                    TAG,
                    "Block reactivation reconciliation failed",
                    e
                )
            }

            blockRepository.observeBlockCandidates()
                .catch { e ->
                    Log.e(TAG, "observeActiveBlocks error", e)

                    withContext(Dispatchers.Main.immediate) {
                        blocks = emptyList()
                    }
                }
                .collect { newBlocks ->
                    withContext(Dispatchers.Main.immediate) {
                        blocks = newBlocks

                        Log.d(TAG, "observeActiveBlocks emitted. blocks=${newBlocks.map { it.packageName }}")

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

        Log.d(TAG, "Event package = $packageName, type = ${event.eventType}")

        if (packageName == applicationContext.packageName) {
            Log.d(TAG, "Ignore WaveTime package. Clear currentPackageName.")
            currentPackageName = null
            return
        }

        if (shouldIgnorePackage(packageName)) {
            Log.d(TAG, "Ignore package = $packageName, keep currentPackageName = $currentPackageName")
            return
        }

        currentPackageName = packageName

        Log.d(TAG, "Set currentPackageName = $currentPackageName")

        checkCurrentApp()
    }

    private fun checkCurrentApp() {
        val packageName = currentPackageName

        Log.d(
            TAG,
            "checkCurrentApp() currentPackageName=$packageName"
        )

        if (packageName == null) {
            Log.d(TAG, "Stop check: currentPackageName is null")
            return
        }

        if (packageName == applicationContext.packageName) {
            Log.d(TAG, "Stop check: package is WaveTime")
            return
        }

        val block = blocks.firstOrNull { candidate ->
            candidate.packageName == packageName
        } ?: run {
            Log.d(
                TAG,
                "No block found for package=$packageName"
            )

            cancelUnlockWatcher()
            return
        }

        val now = System.currentTimeMillis()

        if (!block.shouldBeActive(now)) {
            Log.d(
                TAG,
                "Block is inactive: " +
                        "package=${block.packageName}, " +
                        "isActive=${block.isActive}, " +
                        "reactivateAt=${block.reactivateAt}"
            )

            cancelUnlockWatcher()
            return
        }

        if (block.isReactivationDue(now)) {
            persistReactivation(
                blockId = block.id,
                now = now
            )
        }

        if (block.unlockUntil > now) {
            Log.d(
                TAG,
                "App temporarily unlocked: " +
                        "package=${block.packageName}, " +
                        "remaining=${block.unlockUntil - now}"
            )

            scheduleBlockWhenUnlockExpires(block)
            return
        }

        cancelUnlockWatcher()

        Log.d(
            TAG,
            "Block app now: " +
                    "package=${block.packageName}, " +
                    "isActive=${block.isActive}, " +
                    "reactivateAt=${block.reactivateAt}, " +
                    "unlockUntil=${block.unlockUntil}"
        )

        blockApp(block)
    }

    private fun scheduleBlockWhenUnlockExpires(
        block: Block
    ) {
        if (
            unlockWatcherJob?.isActive == true &&
            watchedBlockId == block.id &&
            watchedUnlockUntil == block.unlockUntil
        ) {
            Log.d(
                TAG,
                "Unlock watcher already scheduled: " +
                        "blockId=${block.id}, " +
                        "unlockUntil=${block.unlockUntil}"
            )
            return
        }

        cancelUnlockWatcher()
        watchedBlockId = block.id
        watchedUnlockUntil = block.unlockUntil

        val delayMs =
            (block.unlockUntil - System.currentTimeMillis())
                    .coerceAtLeast(0L)

        Log.d(
            TAG,
            "Schedule unlock watcher: " +
                    "package=${block.packageName}, " +
                    "delayMs=$delayMs"
        )

        val targetBlockId = block.id
        val targetUnlockUntil = block.unlockUntil

        unlockWatcherJob =
            serviceScope.launch(Dispatchers.Main) {
                try {
                    delay(delayMs)

                    val foregroundPackage =
                        rootInActiveWindow
                            ?.packageName
                            ?.toString()

                    val isTargetStillForeground =
                        foregroundPackage == block.packageName ||
                                (
                                        foregroundPackage == null &&
                                                currentPackageName == block.packageName
                                        )

                    Log.d(
                        TAG,
                        "Unlock watcher fired: " +
                                "target=${block.packageName}, " +
                                "foreground=$foregroundPackage, " +
                                "current=$currentPackageName"
                    )

                    if (isTargetStillForeground) {
                        analyticsLogger.logBlockRelocked(
                            reason = "temporary_unlock_expired"
                        )

                        checkCurrentApp()
                    }
                } finally {
                    /*
                     * Chỉ xóa state nếu đây vẫn là watcher hiện tại.
                     * Tránh coroutine cũ xóa thông tin của watcher mới.
                     */
                    if (
                        watchedBlockId == targetBlockId &&
                        watchedUnlockUntil == targetUnlockUntil
                    ) {
                        unlockWatcherJob = null
                        watchedBlockId = null
                        watchedUnlockUntil = 0L
                    }
                }
            }
    }

    private fun blockApp(block: Block) {
        Log.d(TAG, "blockApp() called for ${block.packageName}")

        if (blockJob?.isActive == true) {
            Log.d(TAG, "blockApp skipped: blockJob is active")
            return
        }

        analyticsLogger.logBlockTriggered(
            blockType = block.blockType,
            isTemporarilyUnlocked = false
        )
        blockJob = serviceScope.launch(Dispatchers.Main) {
            Log.d(TAG, "Starting BlockedActivity for ${block.packageName}")

            val intent = Intent(applicationContext, BlockedActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                putExtra(BlockedActivity.EXTRA_APP_NAME, block.appName)
                putExtra(BlockedActivity.EXTRA_PACKAGE_NAME, block.packageName)
            }

            startActivity(intent)
        }
    }

    private fun getDefaultLauncherPackage(): String? {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
        }

        return packageManager.resolveActivity(intent, 0)
            ?.activityInfo
            ?.packageName
    }

    private fun shouldIgnorePackage(packageName: String): Boolean {
        return packageName == launcherPackageName ||
                packageName == "com.android.launcher" ||
                packageName == "com.android.systemui" ||
                packageName == "com.google.android.googlequicksearchbox"
    }

    private fun persistReactivation(
        blockId: String,
        now: Long
    ) {
        if (!reactivationInProgress.add(blockId)) {
            return
        }

        serviceScope.launch(Dispatchers.IO) {
            try {
                val updated =
                    blockRepository.reactivateBlockIfDue(
                        blockId = blockId,
                        now = now
                    )

                Log.d(
                    TAG,
                    "Reactivate block result: " +
                            "blockId=$blockId, updated=$updated"
                )
            } catch (e: Exception) {
                Log.e(
                    TAG,
                    "Failed to persist block reactivation: " +
                            "blockId=$blockId",
                    e
                )
            } finally {
                withContext(Dispatchers.Main.immediate) {
                    reactivationInProgress.remove(blockId)
                }
            }
        }
    }

    private fun cancelUnlockWatcher() {
        unlockWatcherJob?.cancel()
        unlockWatcherJob = null

        watchedBlockId = null
        watchedUnlockUntil = 0L
    }

    override fun onInterrupt() {
        Log.w(TAG, "Accessibility service interrupted")
    }

    override fun onDestroy() {
        cancelUnlockWatcher()

        blockJob?.cancel()
        blockJob = null

        blocks = emptyList()
        currentPackageName = null

        reactivationInProgress.clear()

        serviceScope.cancel()

        super.onDestroy()
    }
}