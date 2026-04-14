package com.sae.wavetime

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.sae.wavetime.databinding.FragmentMainContentBinding
import com.sae.wavetime.ui.common.DrawerController

class MainContentFragment : Fragment(R.layout.fragment_main_content) {
    private var _binding: FragmentMainContentBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentMainContentBinding.bind(view)

        binding.btnTask.setOnClickListener {
            val navHostFragment =
                childFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

            navHostFragment.navController.navigate(R.id.taskListFragment)
        }

        binding.btnItem.setOnClickListener {
            val navHostFragment =
                childFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

            navHostFragment.navController.navigate(R.id.itemListFragment)
        }

        binding.btnBlock.setOnClickListener {
            val navHostFragment =
                childFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

            navHostFragment.navController.navigate(R.id.blockListFragment)
        }

        binding.btnBar.setOnClickListener {
            (activity as? DrawerController)?.openDrawer()
        }
    }
}