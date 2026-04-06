package com.sae.wavetime.ui.task.create

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sae.wavetime.MainActivity
import com.sae.wavetime.R
import com.sae.wavetime.data.repository.ItemRepository
import com.sae.wavetime.data.repository.TaskRepository
import com.sae.wavetime.databinding.FragmentTaskCreateBinding
import com.sae.wavetime.local.DatabaseProvider
import com.sae.wavetime.ui.task.create.SelectItemDialog
import com.sae.wavetime.ui.task.list.TaskListViewModel
import com.sae.wavetime.ui.task.list.TaskListViewModelFactory
import kotlinx.coroutines.launch
import kotlin.getValue

class TaskCreateFragment : Fragment(R.layout.fragment_task_create) {
    private lateinit var adapter: TaskRewardAdapter
    private var _binding: FragmentTaskCreateBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RewardSelectViewModel by viewModels {
        RewardSelectViewModelFactory(
            ItemRepository(
                DatabaseProvider.getDatabase(requireContext()).itemDao()
            )
        )
    }

    private fun render(state: RewardSelectState) {

        if (state.isLoading) {
            // show loading
        }

        if (state.error != null) {
            // show error
        }
        val filteredList = state.rewards.filter { it.quantity > 0 }

        binding.rvItems.visibility =
            if (filteredList.isEmpty()) View.GONE else View.VISIBLE

        adapter.submitList(filteredList)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentTaskCreateBinding.bind(view)

        adapter = TaskRewardAdapter()

        binding.rvItems.layoutManager = LinearLayoutManager(requireContext())
        binding.rvItems.adapter = adapter

        viewModel.loadRewards()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    render(state)
                }
            }
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnItem.setOnClickListener {
            val dialog = SelectItemDialog { selectedList ->
                viewModel.updateTaskReward(selectedList)
            }
            dialog.show(parentFragmentManager, "SelectItem")
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).showMainUi(false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as MainActivity).showMainUi(true)
        _binding = null
    }
}