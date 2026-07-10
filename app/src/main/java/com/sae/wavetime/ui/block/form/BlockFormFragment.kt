package com.sae.wavetime.ui.block.form

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.sae.wavetime.R
import com.sae.wavetime.data.mapper.toDomain
import com.sae.wavetime.data.repository.BlockRepository
import com.sae.wavetime.data.repository.ItemRepository
import com.sae.wavetime.data.resolver.AppIconResolver
import com.sae.wavetime.data.resolver.InstalledAppResolver
import com.sae.wavetime.databinding.FragmentBlockFormBinding
import com.sae.wavetime.domain.model.Block
import com.sae.wavetime.domain.usecase.CreateBlockWithKeyUseCase
import com.sae.wavetime.local.DatabaseProvider
import com.sae.wavetime.ui.dialog.FeatureGuideDialog
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.getValue

class BlockFormFragment : Fragment(R.layout.fragment_block_form){
    private var _binding: FragmentBlockFormBinding? = null
    private val binding get() = _binding!!
    private var blockId: String? = null

    private var block: Block? = null
    private val viewModel: BlockFormViewModel by viewModels {
        val db = DatabaseProvider.getDatabase(requireContext())

        val blockRepo = BlockRepository(
            db.blockDao(),
            AppIconResolver(requireContext().applicationContext),
            InstalledAppResolver(requireContext().applicationContext)
        )
        val itemRepo = ItemRepository(db.itemDao())

        BlockFormViewModelFactory(
            blockRepo,
            CreateBlockWithKeyUseCase(
                blockRepo,
                itemRepo
            )
        )
    }

    private fun showSelectAppDialog() {
        val dialog = SelectAppDialog { app ->
            viewModel.setSelectedApp(app)

            val icon = AppIconResolver(requireContext()).getIcon(app.packageName)

            binding.edtAppName.setText(app.appName)
            binding.edtPackageName.setText(app.packageName)

            if (icon != null) {
                binding.ivAppIcon.setImageDrawable(icon)
            } else {
                binding.ivAppIcon.setImageResource(R.drawable.default_app)
            }
        }

        dialog.show(childFragmentManager, "SelectApp")
    }

    private fun render(state: BlockFormState) {
        if (state.isLoading) {
            // show loading
        }

        if (state.error != null) {
            // show error
        }

        if (blockId == null) {
            binding.tvTitle.text = getString(R.string.create_block)
        } else {
            binding.tvTitle.text = getString(R.string.edit_block)
            if (blockId != null && binding.edtAppName.text.isNullOrEmpty()) {
                state.selectedApp?.let { block ->
                    val icon = AppIconResolver(requireContext()).getIcon(block.packageName)
                    binding.edtAppName.setText(block.appName)
                    binding.edtPackageName.setText(block.packageName)
                    if (icon != null) {
                        binding.ivAppIcon.setImageDrawable(icon)
                    } else {
                        binding.ivAppIcon.setImageResource(R.drawable.default_app)
                    }
                }
            }
            block = state.selectedApp?.toDomain()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var isClicking = false

        _binding = FragmentBlockFormBinding.bind(view)

        blockId = arguments?.getString("blockId")

        if (blockId == null) {
            binding.tvTitle.text = getString(R.string.create_block)
        } else {
            viewModel.observeBlock(blockId!!)
        }

        binding.btnBasicQuestion.setOnClickListener {
            val dialog = FeatureGuideDialog.newInstance(
                title = getString(R.string.block_basic_guide_title),
                message = getString(R.string.block_basic_guide_message)
            )

            if (parentFragmentManager.findFragmentByTag("FeatureGuideDialog") == null) {
                dialog.show(parentFragmentManager, "FeatureGuideDialog")
            }
        }
        binding.btnTypeQuestion.setOnClickListener {
            val dialog = FeatureGuideDialog.newInstance(
                title = getString(R.string.block_type_guide_title),
                message = getString(R.string.block_type_guide_message)
            )

            if (parentFragmentManager.findFragmentByTag("FeatureGuideDialog") == null) {
                dialog.show(parentFragmentManager, "FeatureGuideDialog")
            }
        }


        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.edtAppName.setOnClickListener {
            if (isClicking) return@setOnClickListener

            isClicking = true
            showSelectAppDialog()

            Handler(Looper.getMainLooper()).postDelayed({
                isClicking = false
            }, 500)
        }

        binding.edtPackageName.setOnClickListener {
            if (isClicking) return@setOnClickListener

            isClicking = true
            showSelectAppDialog()

            Handler(Looper.getMainLooper()).postDelayed({
                isClicking = false
            }, 500)
        }

        binding.btnSuccess.setOnClickListener {
            val appName = binding.edtAppName.text.toString()
            val packageName = binding.edtPackageName.text.toString()
            if (appName.isBlank() && packageName.isBlank()) {
                binding.edtAppName.error = "This field cannot be empty"
                binding.edtPackageName.error = "This field cannot be empty"
                return@setOnClickListener
            }

            if (blockId == null) {
                val blockData = Block(
                    id = UUID.randomUUID().toString(),
                    appName = appName,
                    packageName = packageName,
                )
                viewModel.saveBlock(blockData)
            } else {
                val currentBlockId = blockId
                val blockData = block?.copy(
                    id = currentBlockId!!,
                    appName = appName,
                    packageName = packageName,
                )
                viewModel.saveBlock(blockData!!)
            }

            requireActivity().onBackPressedDispatcher.onBackPressed()

        }

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