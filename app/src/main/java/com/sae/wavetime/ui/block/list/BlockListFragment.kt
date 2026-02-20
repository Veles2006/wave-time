package com.sae.wavetime.ui.block.list

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.sae.wavetime.R
import com.sae.wavetime.data.repository.AppRepository
import com.sae.wavetime.databinding.FragmentBlockListBinding
import kotlinx.coroutines.launch

class BlockListFragment : Fragment(R.layout.fragment_block_list) {

    private lateinit var adapter: AppAdapter
    private var _binding: FragmentBlockListBinding? = null

    private val binding get() = _binding!!

    private val viewModel: BlockListViewModel by viewModels {
        BlockListViewModelFactory(AppRepository(requireContext().applicationContext))
    }

    private fun render(state: BlockListState) {
        if (state.isLoading) {
            // show loading
        }

        if (state.error != null) {
            // show error
        }

        adapter.submitList(state.apps)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentBlockListBinding.bind(view)

        adapter = AppAdapter()

        binding.rvBlocks.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBlocks.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    render(state)
                }
            }
        }

        viewModel.loadApps()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}