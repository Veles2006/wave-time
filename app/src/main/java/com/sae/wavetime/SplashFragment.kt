package com.sae.wavetime

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.sae.wavetime.data.repository.BlockRepository
import com.sae.wavetime.data.repository.InstalledAppRepository
import com.sae.wavetime.data.resolver.AppIconResolver
import com.sae.wavetime.data.resolver.InstalledAppResolver
import com.sae.wavetime.local.DatabaseProvider
import com.sae.wavetime.ui.block.form.InstalledAppViewModel
import com.sae.wavetime.ui.block.form.InstalledAppViewModelFactory
import com.sae.wavetime.ui.block.list.BlockListViewModel
import com.sae.wavetime.ui.block.list.BlockListViewModelFactory

class SplashFragment : Fragment(R.layout.fragment_splash) {

    private val installedAppViewModel: InstalledAppViewModel by activityViewModels {
        val db = DatabaseProvider.getDatabase(requireContext())

        InstalledAppViewModelFactory(
            InstalledAppRepository(
                db.installedAppDao(),
                InstalledAppResolver(requireContext().applicationContext),
                AppIconResolver(requireContext().applicationContext)
            )
        )
    }

    private val blockViewModel: BlockListViewModel by viewModels {
        val db = DatabaseProvider.getDatabase(requireContext())

        BlockListViewModelFactory(
            BlockRepository(
                db.blockDao(),
                AppIconResolver(requireContext().applicationContext),
                InstalledAppResolver(requireContext().applicationContext)
            )
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val blocks = blockViewModel.state.value.blocks

        installedAppViewModel.refresh(blocks)

        findNavController().navigate(R.id.action_splashFragment_to_mainContentFragment)
    }
}