package com.sae.wavetime.ui.block.detail

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.sae.wavetime.MainActivity
import com.sae.wavetime.R
import com.sae.wavetime.data.repository.BlockRepository
import com.sae.wavetime.data.resolver.AppIconResolver
import com.sae.wavetime.data.resolver.InstalledAppResolver
import com.sae.wavetime.databinding.FragmentBlockDetailBinding
import com.sae.wavetime.domain.usecase.DeleteBlockUseCase
import com.sae.wavetime.engine.block.BlockReactivationScheduler
import com.sae.wavetime.local.DatabaseProvider
import com.sae.wavetime.ui.common.toBlockTypeText
import com.sae.wavetime.ui.dialog.SoftDeleteDialog
import kotlinx.coroutines.launch

class BlockDetailFragment : Fragment(R.layout.fragment_block_detail){
    private lateinit var blockId: String
    private var _binding: FragmentBlockDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BlockDetailViewModel by viewModels {
        val db = DatabaseProvider.getDatabase(requireContext())

        val blockRepo = BlockRepository(
            db.blockDao(),
            AppIconResolver(requireContext().applicationContext),
            InstalledAppResolver(requireContext().applicationContext)
        )

        BlockDetailViewModelFactory(
            blockRepo,
            DeleteBlockUseCase(
                db,
                BlockReactivationScheduler(requireContext())
            )
        )
    }

    private fun render(state: BlockDetailState) {

        state.block?.let { block ->
            val icon = AppIconResolver(requireContext()).getIcon(block.packageName)
            binding.tvAppName.text = block.appName
            binding.tvPackageName.text = getString(R.string.package_name_format, block.packageName)
            binding.tvBlockType.text = getString(R.string.block_type_format, block.blockType.toBlockTypeText(requireContext()))
            binding.tvPenaltyMinutes.text = getString(R.string.penalty_minutes_format, block.penaltyMinutes)

            with(binding) {
                val timerLabelRes = when (state.countdownMode) {
                    BlockCountdownMode.NONE -> null

                    BlockCountdownMode.TEMPORARY_UNLOCK ->
                        R.string.temporary_unlock_remaining

                    BlockCountdownMode.REACTIVATION ->
                        R.string.block_reactivates_in
                }

                layoutTimerInfo.isVisible = timerLabelRes != null

                timerLabelRes?.let { labelRes ->
                    tvTimerLabel.setText(labelRes)
                    tvTimer.text = state.remainingTime
                }
            }

            binding.tvTimer.text = state.remainingTime

            if (icon != null) {
                binding.ivAppIcon.setImageDrawable(icon)
            } else {
                binding.ivAppIcon.setImageResource(R.drawable.default_app)
            }

            binding.btnEdit.setOnClickListener {
                (activity as? MainActivity)?.openBlockForm(block.id)
            }
            binding.btnDelete.setOnClickListener {
                val dialog = SoftDeleteDialog.newInstance(
                    title = getString(R.string.delete_block_title),
                    message = getString(R.string.delete_block_message)
                )

                dialog.setOnConfirmListener {
                    viewModel.softDelete(block.id)
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }

                if (parentFragmentManager.findFragmentByTag("SoftDeleteDialog") == null) {
                    dialog.show(parentFragmentManager, "SoftDeleteDialog")
                }
            }
            binding.btnSuccess.setOnClickListener {

                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
            binding.btnBack.setOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentBlockDetailBinding.bind(view)

        blockId = requireArguments().getString("blockId")
            ?: throw IllegalArgumentException("Missing blockId")

        viewModel.observeBlock(blockId)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    render(state)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}