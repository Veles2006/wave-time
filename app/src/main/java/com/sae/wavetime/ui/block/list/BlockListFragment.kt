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
import com.sae.wavetime.domain.usecase.ActivateBlockUseCase
import com.sae.wavetime.domain.usecase.DeleteBlockUseCase
import com.sae.wavetime.domain.usecase.TemporarilyDeactivateBlockUseCase
import com.sae.wavetime.engine.block.BlockReactivationScheduler
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
    private var accessibilityEnabled = false

    private val viewModel: BlockListViewModel by viewModels {
        val db = DatabaseProvider.getDatabase(requireContext())

        BlockListViewModelFactory(
            BlockRepository(
                db.blockDao(),
                AppIconResolver(requireContext().applicationContext),
                InstalledAppResolver(requireContext().applicationContext)
            ),
            ActivateBlockUseCase(
                db,
                BlockReactivationScheduler(requireContext())
            ),
            TemporarilyDeactivateBlockUseCase(
                db,
                BlockReactivationScheduler(requireContext())
            ),
            DeleteBlockUseCase(
                db,
                BlockReactivationScheduler(requireContext())
            )
        )
    }

    private fun render(state: BlockListState) {
        val hasBlocks = state.blocks.isNotEmpty()
        val showContent =
            accessibilityEnabled && hasBlocks

        val showEmpty =
            accessibilityEnabled &&
                    !hasBlocks &&
                    !state.isLoading

        adapter.submitList(state.blocks)

        with(binding) {
            tvAccessibilityInfo.isVisible = !accessibilityEnabled
            tvAccessibilityInstruction.isVisible = !accessibilityEnabled
            btnOpenAccessibility.isVisible = !accessibilityEnabled

            btnCreateBlock.isVisible = accessibilityEnabled

            rvBlocks.isVisible = showContent
            tvEmptyBlock.isVisible = showEmpty
            ivEmptyBlock.isVisible = showEmpty
        }
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

    private fun handleActiveChangeRequest(
        app: AppUiModel,
        requestedActive: Boolean
    ) {
        if (requestedActive) {
            viewModel.changeBlockActive(
                blockId = app.id,
                isActive = true
            )
        } else {
            showDeactivateBlockDialog(app)
        }
    }

    private fun showDeactivateBlockDialog(
        app: AppUiModel
    ) {
        DeactivateBlockDialog.newInstance(
            initialTitle = getString(
                R.string.deactivate_block_title
            ),
            initialMessage = getString(
                R.string.deactivate_block_message
            ),
            warningTitle = getString(
                R.string.deactivate_block_warning_title
            ),
            warningMessage = getString(
                R.string.deactivate_block_warning_message
            )
        ).apply {
            setOnDeactivateListener {
                viewModel.changeBlockActive(
                    blockId = app.id,
                    isActive = false
                )
            }
        }.show(
            childFragmentManager,
            DeactivateBlockDialog.TAG
        )
    }

    private fun updateAccessibilityState() {
        val newValue =
            requireContext().isAccessibilityServiceEnabled(
                FocusAccessibilityService::class.java
            )

        if (newValue == accessibilityEnabled) {
            return
        }

        accessibilityEnabled = newValue
        render(viewModel.state.value)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentBlockListBinding.bind(view)

        adapter = AppAdapter (
            onLongClick = { app ->
                showBlockOptionsDialog(app)
            },
            onActiveChangeRequested = { app, requestedActive ->
                handleActiveChangeRequest(
                    app = app,
                    requestedActive = requestedActive
                )
            },
            openBlockDetail = { id ->
                (activity as? MainActivity)?.openBlockDetail(id)
            }
        )

        accessibilityEnabled =
            requireContext().isAccessibilityServiceEnabled(
                FocusAccessibilityService::class.java
            )

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

    override fun onResume() {
        super.onResume()

        updateAccessibilityState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}