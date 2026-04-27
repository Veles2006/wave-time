package com.sae.wavetime.ui.item.list

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.sae.wavetime.R
import com.sae.wavetime.data.repository.BlockRepository
import com.sae.wavetime.data.repository.InventoryRepository
import com.sae.wavetime.data.repository.ItemRepository
import com.sae.wavetime.databinding.FragmentItemListBinding
import com.sae.wavetime.domain.usecase.UseItemUseCase
import com.sae.wavetime.local.DatabaseProvider
import kotlinx.coroutines.launch

class ItemListFragment : Fragment(R.layout.fragment_item_list) {

    private lateinit var adapter: ItemAdapter
    private var _binding: FragmentItemListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ItemListViewModel by viewModels {
        val db = DatabaseProvider.getDatabase(requireContext())
        val itemRepo = ItemRepository(db.itemDao())
        val inventoryRepo = InventoryRepository(db.inventoryDao())
        val blockRepo = BlockRepository(db.blockDao())

        ItemListViewModelFactory(
            inventoryRepo,
            UseItemUseCase(
                itemRepo,
                inventoryRepo,
                blockRepo,
                db
            )
        )
    }

    private fun render(state: ItemListState) {

        if (state.isLoading) {
            // show loading
        }

        if (state.error != null) {
            // show error
        }

        adapter.submitList(state.items)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentItemListBinding.bind(view)

        adapter = ItemAdapter{
            itemId, amount ->
            viewModel.useItem(itemId, amount)
        }

        binding.rvItems.layoutManager = LinearLayoutManager(requireContext())
        binding.rvItems.adapter = adapter

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