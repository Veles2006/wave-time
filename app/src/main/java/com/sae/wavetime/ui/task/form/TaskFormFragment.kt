package com.sae.wavetime.ui.task.form

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.sae.wavetime.R
import com.sae.wavetime.data.mapper.toRewardItemList
import com.sae.wavetime.data.mapper.toRewardSelectUiModelList
import com.sae.wavetime.data.model.api.Penalty
import com.sae.wavetime.data.model.api.Reward
import com.sae.wavetime.data.model.api.Task
import com.sae.wavetime.data.repository.ItemRepository
import com.sae.wavetime.data.repository.TaskRepository
import com.sae.wavetime.databinding.FragmentTaskFormBinding
import com.sae.wavetime.local.DatabaseProvider
import com.sae.wavetime.ui.task.list.TaskListViewModel
import com.sae.wavetime.ui.task.list.TaskListViewModelFactory
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.getValue

class TaskFormFragment : Fragment(R.layout.fragment_task_form) {
    private lateinit var adapter: TaskFormRewardAdapter
    private var _binding: FragmentTaskFormBinding? = null
    private val binding get() = _binding!!
    private var task: Task? = null

    private var coinValue = 0
    private var expValue = 0

    private var taskId: String? = null

    private val viewModel: TaskFormViewModel by viewModels {
        TaskFormViewModelFactory(
            TaskRepository(
                DatabaseProvider.getDatabase(requireContext()).taskDao()
            ),
            ItemRepository(
                DatabaseProvider.getDatabase(requireContext()).itemDao()
            )
        )
    }
    private fun render(state: TaskFormState) {

        if (state.isLoading) {
            // show loading
        }

        if (state.error != null) {
            // show error
        }
        var filteredList = state.selectedRewards

        binding.rvItems.visibility =
            if (filteredList.isEmpty()) View.GONE else View.VISIBLE

        binding.btnItem.setText(
            if (filteredList.isEmpty())
                getString(R.string.not_set)
            else
                getString(R.string.change_item))

        adapter.submitList(filteredList)


        if (taskId == null) {
            binding.tvTitle.text = getString(R.string.create_task)
        } else {
            Log.d("TEST", "$taskId")
            binding.tvTitle.text = getString(R.string.edit_task)
            if (taskId != null && binding.edtTaskName.text.isNullOrEmpty()) {
                state.task?.let { task ->
                    Log.d("TEST", "$task")
                    binding.edtTaskName.setText(task.name)
                    binding.edtTaskDesc.setText(task.description)
                    binding.btnCoin.text = task.reward.gold.toString()
                    binding.btnExp.text = task.reward.exp.toString()
                    binding.btnItem.text = getString(R.string.change_item)
                    coinValue = task.reward.gold
                    expValue = task.reward.exp
                }
            }
            task = state.task
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentTaskFormBinding.bind(view)

        taskId = arguments?.getString("taskId")


        adapter = TaskFormRewardAdapter()

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

        if (taskId == null) {
            binding.tvTitle.text = getString(R.string.create_task)
        } else {
            viewModel.observeTask(taskId!!)
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

            if (taskId == null) {
                val taskData = Task(
                    id = UUID.randomUUID().toString(),
                    name = taskName,
                    description = taskDesc,
                    status = "pending",
                    difficulty = taskDifficulty,
                    reward = Reward(
                        gold = coinValue,
                        exp = expValue,
                        items = taskItemReward.toRewardItemList()
                    ),
                    penalty = Penalty(),
                )
                viewModel.addTask(taskData)
            } else {
                val taskData = task?.copy(
                    name = taskName,
                    description = taskDesc,
                    status = "pending",
                    difficulty = taskDifficulty,
                    reward = Reward(
                        gold = coinValue,
                        exp = expValue,
                        items = taskItemReward.toRewardItemList()
                    ),
                    penalty = Penalty(),
                )
                viewModel.updateFullTask(taskData!!)
            }

            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}