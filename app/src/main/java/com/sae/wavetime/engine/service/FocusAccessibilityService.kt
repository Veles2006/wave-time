package com.sae.wavetime.engine.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
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
import com.sae.wavetime.BlockedActivity
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

class FocusAccessibilityService : AccessibilityService() {

    companion object {
        private const val TAG = "FocusService"
    }

    private var launcherPackageName: String? = null

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

        launcherPackageName = getDefaultLauncherPackage()
        Log.d(TAG, "Default launcher = $launcherPackageName")

        serviceScope.launch {
            blockRepo.observeActiveBlocks()
                .catch { e ->
                    Log.e(TAG, "observeActiveBlocks error", e)
                    blocks = emptyList()
                }
                .collect { newBlocks ->
                    blocks = newBlocks

                    Log.d(TAG, "observeActiveBlocks emitted. blocks=${newBlocks.map { it.packageName }}")

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

        Log.d(TAG, "checkCurrentApp() currentPackageName = $packageName")

        if (packageName == null) {
            Log.d(TAG, "Stop check: currentPackageName is null")
            return
        }

        if (packageName == applicationContext.packageName) {
            Log.d(TAG, "Stop check: package is WaveTime")
            return
        }

        Log.d(TAG, "Active blocks count = ${blocks.size}")

        val block = blocks.find {
            it.packageName == packageName
        }

        if (block == null) {
            Log.d(TAG, "No block found for package = $packageName")
            return
        }

        val now = System.currentTimeMillis()

        Log.d(
            TAG,
            "Block found. package=${block.packageName}, now=$now, unlockUntil=${block.unlockUntil}, diff=${block.unlockUntil - now}"
        )

        if (now < block.unlockUntil) {
            Log.d(TAG, "App is temporarily unlocked. Schedule blocker.")
            scheduleBlockWhenUnlockExpires(block)
            return
        }

        Log.d(TAG, "Unlock expired or not unlocked. Blocking app now.")
        blockApp(block)
    }

    private fun scheduleBlockWhenUnlockExpires(block: Block) {
        unlockWatcherJob?.cancel()

        val delayMs = (block.unlockUntil - System.currentTimeMillis())
            .coerceAtLeast(0L)

        Log.d(
            TAG,
            "scheduleBlockWhenUnlockExpires(): package=${block.packageName}, delayMs=$delayMs"
        )

        unlockWatcherJob = serviceScope.launch(Dispatchers.Main) {
            delay(delayMs)

            val foregroundPackage = rootInActiveWindow
                ?.packageName
                ?.toString()

            Log.d(
                TAG,
                "Unlock watcher fired. currentPackageName=$currentPackageName, foregroundPackage=$foregroundPackage, target=${block.packageName}"
            )

            val isTargetStillForeground =
                if (foregroundPackage != null) {
                    foregroundPackage == block.packageName
                } else {
                    currentPackageName == block.packageName
                }

            if (isTargetStillForeground) {
                Log.d(TAG, "Target app still foreground. Re-check now.")
                checkCurrentApp()
            } else {
                Log.d(TAG, "Target app not foreground. Skip block.")
            }
        }
    }

    private fun blockApp(block: Block) {
        Log.d(TAG, "blockApp() called for ${block.packageName}")

        if (blockJob?.isActive == true) {
            Log.d(TAG, "blockApp skipped: blockJob is active")
            return
        }

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

    override fun onInterrupt() {
        // Không cần làm gì tạm thời
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }
}