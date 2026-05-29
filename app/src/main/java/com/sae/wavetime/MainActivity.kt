package com.sae.wavetime

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    openTaskHistory()
                    true
                }

                else -> false
            }
        }
    }
}