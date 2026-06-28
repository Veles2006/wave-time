package com.sae.wavetime

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.sae.wavetime.databinding.ActivityMainBinding
import com.sae.wavetime.local.AppDataStore
import com.sae.wavetime.local.ThemeMode
import com.sae.wavetime.ui.common.DrawerController
import com.sae.wavetime.utils.ThemeManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity(), DrawerController {
    private lateinit var binding: ActivityMainBinding
    private lateinit var appDataStore: AppDataStore

    override fun openDrawer() {
        binding.drawerLayout.openDrawer(GravityCompat.START)
    }
    fun openTaskForm(taskId: String? = null) {
        val bundle = Bundle().apply {
            putString("taskId", taskId)
        }

        findNavController(R.id.nav_host_root)
            .navigate(R.id.action_to_taskForm, bundle)
    }

    fun openTaskDetail(taskId: String) {
        val bundle = Bundle().apply {
            putString("taskId", taskId)
        }

        findNavController(R.id.nav_host_root)
            .navigate(R.id.action_to_taskDetail, bundle)
    }

    fun openTaskHistory() {
        findNavController(R.id.nav_host_root)
            .navigate(R.id.action_to_taskHistory)
    }


    fun openBlockForm(blockId: String? = null) {
        val bundle = Bundle().apply {
            putString("blockId", blockId)
        }

        findNavController(R.id.nav_host_root)
            .navigate(R.id.action_to_blockForm, bundle)
    }

    fun openBlockDetail(blockId: String) {
        val bundle = Bundle().apply {
            putString("blockId", blockId)
        }

        findNavController(R.id.nav_host_root)
            .navigate(R.id.action_to_blockDetail, bundle)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        appDataStore = AppDataStore(applicationContext)

        val themeMode = runBlocking {
            appDataStore.themeModeFlow.first()
        }

        ThemeManager.applyTheme(themeMode)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                Color.TRANSPARENT,
                Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.light(
                Color.TRANSPARENT,
                Color.TRANSPARENT
            )
        )

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_history -> {
                    binding.drawerLayout.closeDrawer(GravityCompat.START, false)
                    openTaskHistory()
                    true
                }
                R.id.menu_language -> {
                    showLanguageDialog()
                    true
                }
                R.id.menu_theme -> {
                    showThemeDialog()
                    true
                }
                else -> false
            }
        }
    }

    private fun setLanguage(languageTag: String?) {
        if (languageTag != null) {
            val appLocale = LocaleListCompat.forLanguageTags(languageTag)
            AppCompatDelegate.setApplicationLocales(appLocale)
        } else {
            resetToSystemLanguage()
        }
    }

    private fun resetToSystemLanguage() {
        AppCompatDelegate.setApplicationLocales(
            LocaleListCompat.getEmptyLocaleList()
        )
    }

    private fun showLanguageDialog() {
        val languages = arrayOf(getString(R.string.default_text), "English", "Tiếng Việt", "繁體中文")
        val languageTags = arrayOf(null, "en", "vi", "zh-TW")

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.select_your_language))
            .setItems(languages) { dialog, which ->
                val selectedLanguageTag = languageTags[which]
                setLanguage(selectedLanguageTag)

                dialog.dismiss()
            }
            .show()
    }

    private fun showThemeDialog() {
        val themes = arrayOf(
            getString(R.string.default_text),
            getString(R.string.theme_light),
            getString(R.string.theme_dark)
        )

        val themeModes = arrayOf(
            ThemeMode.SYSTEM,
            ThemeMode.LIGHT,
            ThemeMode.DARK
        )

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.select_theme))
            .setItems(themes) { dialog, which ->
                val selectedThemeMode = themeModes[which]

                lifecycleScope.launch {
                    appDataStore.saveThemeMode(selectedThemeMode)
                    ThemeManager.applyTheme(selectedThemeMode)
                }

                dialog.dismiss()
            }
            .show()
    }
}