package com.sae.wavetime.ui.block.list

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.sae.wavetime.MainActivity
import com.sae.wavetime.R
import com.sae.wavetime.data.repository.BlockRepository
import com.sae.wavetime.data.resolver.AppIconResolver
import com.sae.wavetime.data.resolver.InstalledAppResolver
import com.sae.wavetime.databinding.FragmentBlockListBinding
import com.sae.wavetime.engine.service.FocusAccessibilityService
import com.sae.wavetime.local.DatabaseProvider
import com.sae.wavetime.ui.dialog.SoftDeleteDialog
import com.sae.wavetime.ui.model.AppUiModel
import com.sae.wavetime.utils.isAccessibilityServiceEnabled
import com.sae.wavetime.utils.openAccessibilitySettings
import kotlinx.coroutines.launch

class BlockListFragment : Fragment(R.layout.fragment_block_list) {

    private lateinit var adapter: AppAdapter
    private var _binding: FragmentBlockListBinding? = null

    private val binding get() = _binding!!

    private var enabled: Boolean = false

    private val viewModel: BlockListViewModel by viewModels {
        BlockListViewModelFactory(
            BlockRepository(
                DatabaseProvider.getDatabase(requireContext()).blockDao(),
                AppIconResolver(requireContext().applicationContext),
                InstalledAppResolver(requireContext().applicationContext)
            ))
    }

    private fun render(state: BlockListState) {
        val accessibilityEnabled = requireContext().isAccessibilityServiceEnabled(
            FocusAccessibilityService::class.java
        )

        adapter.submitList(state.blocks)

        binding.tvAccessibilityInfo.isVisible = !accessibilityEnabled
        binding.tvAccessibilityInstruction.isVisible = !accessibilityEnabled
        binding.btnOpenAccessibility.isVisible = !accessibilityEnabled
        binding.btnCreateBlock.isVisible = accessibilityEnabled

        binding.rvBlocks.isVisible =
            accessibilityEnabled && state.blocks.isNotEmpty()


        binding.tvEmptyBlock.isVisible =
            accessibilityEnabled && state.blocks.isEmpty() && !state.isLoading

        binding.ivEmptyBlock.isVisible =
            accessibilityEnabled && state.blocks.isEmpty() && !state.isLoading
    }

    private fun showBlockOptionsDialog(app: AppUiModel) {
        val options = arrayOf(getString(R.string.edit), getString(R.string.delete))

        AlertDialog.Builder(requireContext())
            .setTitle(app.appName)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        (activity as? MainActivity)?.openBlockForm(app.id)
                    }

                    1 -> {
                        val dialog = SoftDeleteDialog.newInstance(
                            title = getString(R.string.delete_block_title),
                            message = getString(R.string.delete_block_message)
                        )

                        dialog.setOnConfirmListener {
                            viewModel.softDelete(app.id)
                        }

                        if (parentFragmentManager.findFragmentByTag("SoftDeleteDialog") == null) {
                            dialog.show(parentFragmentManager, "SoftDeleteDialog")
                        }
                    }
                }
            }
            .show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentBlockListBinding.bind(view)

        adapter = AppAdapter (
            onLongClick = { app ->
                showBlockOptionsDialog(app)
            },
            onToggleActivity = { id, isChecked ->
                viewModel.setActive(id, isChecked)
            },
            openBlockDetail = { id ->
                (activity as? MainActivity)?.openBlockDetail(id)
            }
        )

        viewModel.loadBlocks()

        binding.rvBlocks.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBlocks.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    render(state)
                }
            }
        }

        binding.btnCreateBlock.setOnClickListener {
            (activity as? MainActivity)?.openBlockForm()
        }

        binding.btnOpenAccessibility.setOnClickListener {
            requireContext().openAccessibilitySettings()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}