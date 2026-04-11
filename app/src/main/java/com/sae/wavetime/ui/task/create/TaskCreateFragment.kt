package com.sae.wavetime.ui.task.create

import android.R.attr.text
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sae.wavetime.MainActivity
import com.sae.wavetime.R
import com.sae.wavetime.data.mapper.toRewardItemList
import com.sae.wavetime.data.model.api.Penalty
import com.sae.wavetime.data.model.api.Reward
import com.sae.wavetime.data.model.api.Task
import com.sae.wavetime.data.repository.ItemRepository
import com.sae.wavetime.data.repository.TaskRepository
import com.sae.wavetime.databinding.FragmentTaskCreateBinding
import com.sae.wavetime.local.DatabaseProvider
import com.sae.wavetime.ui.task.list.TaskListViewModel
import com.sae.wavetime.ui.task.list.TaskListViewModelFactory
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.getValue

class TaskCreateFragment : Fragment(R.layout.fragment_task_create) {
    private lateinit var adapter: TaskRewardAdapter
    private var _binding: FragmentTaskCreateBinding? = null
    private val binding get() = _binding!!

    private var coinValue = 0
    private var expValue = 0

    private val viewModel: RewardSelectViewModel by viewModels {
        RewardSelectViewModelFactory(
            ItemRepository(
                DatabaseProvider.getDatabase(requireContext()).itemDao()
            )
        )
    }

    private val taskViewModel: TaskListViewModel by activityViewModels  {
        TaskListViewModelFactory(
            TaskRepository(
                DatabaseProvider.getDatabase(requireContext()).taskDao()
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
        val filteredList = state.selectedRewards

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
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.btnCoin.setOnClickListener {
            val dialog = CoinExperienceDialog { text ->
                coinValue = text.toIntOrNull() ?: 0
                binding.btnCoin.text = "$coinValue Coin"
            }
            if (parentFragmentManager.findFragmentByTag("CoinExperience") == null) {
                dialog.show(parentFragmentManager, "CoinExperience")
            }
        }

        binding.btnExp.setOnClickListener {
            val dialog = CoinExperienceDialog { text ->
                expValue = text.toIntOrNull() ?: 0
                binding.btnExp.text = "$expValue EXP"
            }
            if (parentFragmentManager.findFragmentByTag("CoinExperience") == null) {
                dialog.show(parentFragmentManager, "CoinExperience")
            }
        }

        binding.btnItem.setOnClickListener {
            val dialog = SelectItemDialog { selectedList ->
                viewModel.updateTaskReward(selectedList)
            }
            dialog.show(parentFragmentManager, "SelectItem")
        }

        binding.btnSuccess.setOnClickListener {

            val taskName = binding.edtTaskName.text.toString()
            val taskDesc = binding.edtTaskDesc.text?.toString()?.trim()
            val taskItemReward = viewModel.state.value.selectedRewards

            val taskDifficulty = when (binding.sliderDifficulty.value.toInt()) {
                1 -> "Mortal"
                2 -> "Yao"
                3 -> "Gui"
                4 -> "Mara"
                else -> "Unknown"
            }

            if (taskName.isBlank()) {
                binding.edtTaskName.error = "This field cannot be empty"
                return@setOnClickListener
            }

            taskViewModel.addTask(
                Task(
                    id = UUID.randomUUID().toString(),
                    name = taskName,
                    description = taskDesc,
                    difficulty = taskDifficulty,
                    reward = Reward(
                        gold = coinValue,
                        exp = expValue,
                        items = taskItemReward.toRewardItemList()
                    ),
                    penalty = Penalty(),
                )
            )

            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}