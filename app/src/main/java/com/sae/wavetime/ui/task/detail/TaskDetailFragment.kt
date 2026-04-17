package com.sae.wavetime.ui.task.detail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.sae.wavetime.R
import com.sae.wavetime.data.repository.TaskRepository
import com.sae.wavetime.databinding.FragmentTaskDetailBinding
import com.sae.wavetime.local.DatabaseProvider
import com.sae.wavetime.utils.toDisplayString
import kotlinx.coroutines.launch

class TaskDetailFragment : Fragment(R.layout.fragment_task_detail) {
    private lateinit var taskId: String
    private var _binding: FragmentTaskDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TaskDetailViewModel by viewModels {
        TaskDetailModelFactory(
            TaskRepository(
                DatabaseProvider.getDatabase(requireContext()).taskDao()
            )
        )
    }

    private fun render(state: TaskDetailState) {
        if (state.isLoading) {
            // show loading
        }

        if (state.error != null) {
            // show error
        }

        state.task?.let { task ->
            binding.tvTaskName.text = task.name
            binding.tvDescription.text = "Description: ${task.description}"
            binding.tvStatus.text = "Status: ${task.status}"
            binding.tvDate.text = "Date: ${task.date}"
            binding.tvDifficulty.text = "Difficulty: ${task.difficulty}"
            binding.tvReward.text = "Reward: ${task.reward.toDisplayString()}"
            binding.tvPenalty.text = "Penalty: ${task.penalty.toDisplayString()}"
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentTaskDetailBinding.bind(view)

        taskId = requireArguments().getString("taskId")
            ?: throw IllegalArgumentException("Missing taskId")

        viewModel.observeTask(taskId)

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