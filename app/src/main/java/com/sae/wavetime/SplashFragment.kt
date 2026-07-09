package com.sae.wavetime

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.sae.wavetime.data.repository.BlockRepository
import com.sae.wavetime.data.repository.InstalledAppRepository
import com.sae.wavetime.data.repository.TaskRepository
import com.sae.wavetime.data.resolver.AppIconResolver
import com.sae.wavetime.data.resolver.InstalledAppResolver
import com.sae.wavetime.engine.timer.TaskTimerRecoveryManager
import com.sae.wavetime.local.DatabaseProvider
import com.sae.wavetime.ui.block.form.InstalledAppViewModel
import com.sae.wavetime.ui.block.form.InstalledAppViewModelFactory
import com.sae.wavetime.ui.viewmodel.SplashViewModel
import com.sae.wavetime.ui.viewmodel.SplashViewModelFactory
import kotlinx.coroutines.launch

class SplashFragment : Fragment(R.layout.fragment_splash) {

    private val viewModel: SplashViewModel by viewModels {
        val db = DatabaseProvider.getDatabase(requireContext())
        SplashViewModelFactory(TaskRepository(
            db.taskDao(),
            db.taskTemplateDao()
        ))
    }

    private val installedAppViewModel: InstalledAppViewModel by activityViewModels {
        val db = DatabaseProvider.getDatabase(requireContext())

        InstalledAppViewModelFactory(
            InstalledAppRepository(
                db.installedAppDao(),
                InstalledAppResolver(requireContext().applicationContext),
                AppIconResolver(requireContext().applicationContext)
            ),
            BlockRepository(
                db.blockDao(),
                AppIconResolver(requireContext().applicationContext),
                InstalledAppResolver(requireContext().applicationContext)
            )
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        installedAppViewModel.refresh()

        viewLifecycleOwner.lifecycleScope.launch {
            TaskTimerRecoveryManager.recover(requireContext())

            viewModel.prepareTodayTasks()

            findNavController().navigate(R.id.action_splashFragment_to_mainContentFragment)
        }


    }
}