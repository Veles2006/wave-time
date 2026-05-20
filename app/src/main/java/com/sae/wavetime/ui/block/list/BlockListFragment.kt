package com.sae.wavetime.ui.block.list

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
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
import com.sae.wavetime.local.DatabaseProvider
import com.sae.wavetime.ui.model.AppUiModel
import kotlinx.coroutines.launch

class BlockListFragment : Fragment(R.layout.fragment_block_list) {

    private lateinit var adapter: AppAdapter
    private var _binding: FragmentBlockListBinding? = null

    private val binding get() = _binding!!

    private val viewModel: BlockListViewModel by viewModels {
        BlockListViewModelFactory(
            BlockRepository(
                DatabaseProvider.getDatabase(requireContext()).blockDao(),
                AppIconResolver(requireContext().applicationContext),
                InstalledAppResolver(requireContext().applicationContext)
            ))
    }

    private fun render(state: BlockListState) {
        if (state.isLoading) {
            // show loading
        }

        if (state.error != null) {
            // show error
        }

        adapter.submitList(state.blocks)
    }

    private fun showBlockOptionsDialog(app: AppUiModel) {
        val options = arrayOf("Chỉnh sửa", "Xoá")

        AlertDialog.Builder(requireContext())
            .setTitle(app.appName)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        (activity as? MainActivity)?.openBlockForm(app.id)
                    }

                    1 -> {
                        viewModel.softDelete(app.id)
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}