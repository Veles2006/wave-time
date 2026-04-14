package com.sae.wavetime

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import com.sae.wavetime.databinding.ActivityMainBinding
import com.sae.wavetime.ui.common.DrawerController
import com.sae.wavetime.ui.task.create.TaskCreateFragment

class MainActivity : AppCompatActivity(), DrawerController {
    private lateinit var binding: ActivityMainBinding

    override fun openDrawer() {
        binding.drawerLayout.openDrawer(GravityCompat.START)
    }
    fun openTaskCreate() {
        findNavController(R.id.nav_host_root)
            .navigate(R.id.action_to_taskCreate)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        binding.fullscreenContainer.visibility = View.GONE

        // 🔥 Điều khiển system bars
//        val controller = WindowCompat.getInsetsController(window, window.decorView)
//
//        controller.isAppearanceLightStatusBars = true
//        controller.isAppearanceLightNavigationBars = true
//
//        // 🔥 Cho phép nội dung vẽ ra sau system bars
//        WindowCompat.setDecorFitsSystemWindows(window, false)
//
//        // 🔥 Handle padding
//        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//
//            binding.layoutHeader.setPadding(0, systemBars.top, 0, 0)
//            binding.bottomBar.setPadding(0, 0, 0, systemBars.bottom)
//
//            insets
//        }

//        binding.btnTask.setOnClickListener {
//            val navController = findNavController(R.id.nav_host_fragment)
//            navController.navigate(R.id.taskListFragment)
//        }
//
//        binding.btnItem.setOnClickListener {
//            val navController = findNavController(R.id.nav_host_fragment)
//            navController.navigate(R.id.itemListFragment)
//        }
//
//        binding.btnBlock.setOnClickListener {
//            val navController = findNavController(R.id.nav_host_fragment)
//            navController.navigate(R.id.blockListFragment)
//        }

//        binding.btnBar.setOnClickListener {
//            binding.drawerLayout.openDrawer(GravityCompat.START)
//        }

//        onBackPressedDispatcher.addCallback(
//            this,
//            object : OnBackPressedCallback(true) {
//                override fun handleOnBackPressed() {
//                    if (supportFragmentManager.backStackEntryCount > 0 &&
//                        binding.fullscreenContainer.visibility == View.VISIBLE
//                        ) {
//                        supportFragmentManager.popBackStack()
//
//                        binding.fullscreenContainer.postDelayed({
//                            binding.fullscreenContainer.visibility = View.GONE
//                        }, 300)
//                    } else {
//                        isEnabled = false
//                        onBackPressedDispatcher.onBackPressed()
//                    }
//                }
//            }
//        )
    }
}