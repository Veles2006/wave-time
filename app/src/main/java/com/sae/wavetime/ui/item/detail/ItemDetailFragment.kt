package com.sae.wavetime.ui.item.detail

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.sae.wavetime.R
import com.sae.wavetime.analytics.AnalyticsTracker
import com.sae.wavetime.data.repository.BlockRepository
import com.sae.wavetime.data.repository.InventoryRepository
import com.sae.wavetime.data.repository.ItemRepository
import com.sae.wavetime.data.resolver.AppIconResolver
import com.sae.wavetime.data.resolver.InstalledAppResolver
import com.sae.wavetime.databinding.FragmentItemDetailBinding
import com.sae.wavetime.domain.usecase.ObserveInventoryDetailUseCase
import com.sae.wavetime.domain.usecase.UseItemUseCase
import com.sae.wavetime.local.DatabaseProvider
import com.sae.wavetime.ui.common.getLocalizedDescription
import com.sae.wavetime.ui.common.toKeyTranslate
import com.sae.wavetime.ui.common.toLocalizedKeyName
import com.sae.wavetime.ui.common.toTierText
import kotlinx.coroutines.launch

class ItemDetailFragment : Fragment(R.layout.fragment_item_detail) {
    private lateinit var itemId: String
    private var _binding: FragmentItemDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ItemDetailViewModel by viewModels {
        val db = DatabaseProvider.getDatabase(requireContext())
        val itemRepo = ItemRepository(db.itemDao())
        val inventoryRepo = InventoryRepository(db.inventoryDao())
        val blockRepo = BlockRepository(
            db.blockDao(),
            AppIconResolver(requireContext().applicationContext),
            InstalledAppResolver(requireContext().applicationContext)
        )

        ItemDetailViewModelFactory(
            itemRepo,
            UseItemUseCase(
                itemRepo,
                inventoryRepo,
                blockRepo,
                db,
                analyticsLogger = AnalyticsTracker(requireContext())
            ),
            ObserveInventoryDetailUseCase(
                inventoryRepo,
                blockRepo
            )
        )
    }

    private fun render(state: ItemDetailState) {
        val inventoryItem = state.inventoryItem ?: return
        val blockName = inventoryItem.blockName


        binding.tvItemName.text = inventoryItem.name.toLocalizedKeyName(requireContext())
        binding.tvDescription.text =
            inventoryItem.getLocalizedDescription(requireContext())

        binding.tvTier.text = getString(
            R.string.item_tier_format,
            inventoryItem.tier.toTierText(requireContext())
        )

        binding.tvRank.text = getString(
            R.string.item_rank_format,
            inventoryItem.rank
        )

        binding.tvCategory.text = getString(
            R.string.item_category_format,
            inventoryItem.category.toKeyTranslate(requireContext())
        )

        binding.tvBlockName.text = getString(
            R.string.item_block_name_format,
            inventoryItem.blockName
        )

        binding.tvDuration.text = getString(
            R.string.item_duration_minutes_format,
            inventoryItem.durationMinutes
        )
        binding.tvBlockName.isVisible = blockName != null
        binding.ivBlockName.isVisible = blockName != null
        binding.tvDuration.isVisible = blockName != null
        binding.ivDuration.isVisible = blockName != null
        binding.tvQuantity.text = getString(
            R.string.item_remaining_quantity_format,
            inventoryItem.quantity
        )

        binding.btnUseItem.isEnabled = inventoryItem.quantity > 0
        binding.btnUseItem.setOnClickListener {
            if (inventoryItem.quantity <= 0) {
                return@setOnClickListener
            }

            viewModel.useItem(inventoryItem.itemId, 1)
        }
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentItemDetailBinding.bind(view)

        itemId = requireArguments().getString("itemId")
            ?: throw IllegalArgumentException("Missing itemId")

        viewModel.observeInventoryItem(itemId)

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