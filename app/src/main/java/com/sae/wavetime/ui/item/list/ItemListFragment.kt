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
import com.sae.wavetime.data.repository.ItemRepository
import com.sae.wavetime.databinding.FragmentItemListBinding
import kotlinx.coroutines.launch

class ItemListFragment : Fragment(R.layout.fragment_item_list) {

    private lateinit var adapter: ItemAdapter
    private var _binding: FragmentItemListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ItemListViewModel by viewModels {
        ItemListViewModelFactory(ItemRepository())
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

        adapter = ItemAdapter()

        binding.rvItems.layoutManager = LinearLayoutManager(requireContext())
        binding.rvItems.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    render(state)
                }
            }
        }

        viewModel.loadItems()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}