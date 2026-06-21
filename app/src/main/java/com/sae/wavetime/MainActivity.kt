package com.sae.wavetime

import android.app.AlertDialog
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.core.view.GravityCompat
import androidx.navigation.findNavController
import com.sae.wavetime.databinding.ActivityMainBinding
import com.sae.wavetime.ui.common.DrawerController

class MainActivity : AppCompatActivity(), DrawerController {
    private lateinit var binding: ActivityMainBinding

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
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

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
}