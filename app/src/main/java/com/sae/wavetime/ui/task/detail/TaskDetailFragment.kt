package com.sae.wavetime.ui.task.detail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.sae.wavetime.MainActivity
import com.sae.wavetime.R
import com.sae.wavetime.data.repository.InventoryRepository
import com.sae.wavetime.data.repository.TaskRepository
import com.sae.wavetime.databinding.FragmentTaskDetailBinding
import com.sae.wavetime.domain.usecase.CompleteTaskUseCase
import com.sae.wavetime.engine.notification.TaskTimerNotification
import com.sae.wavetime.engine.service.TaskTimerService
import com.sae.wavetime.local.DatabaseProvider
import com.sae.wavetime.ui.dialog.SoftDeleteDialog
import com.sae.wavetime.utils.toDisplayString
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TaskDetailFragment : Fragment(R.layout.fragment_task_detail) {
    private lateinit var taskId: String
    private var _binding: FragmentTaskDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TaskDetailViewModel by viewModels {
        val db = DatabaseProvider.getDatabase(requireContext())

        val taskRepo = TaskRepository(
            db.taskDao(),
            db.taskTemplateDao()
        )
        val inventoryRepo = InventoryRepository(db.inventoryDao())

        TaskDetailModelFactory(
            taskRepo,
            CompleteTaskUseCase(
                taskRepo,           // ✔ dùng lại
                inventoryRepo,
                db
            )
        )
    }

    private fun formatRemainingTime(millis: Long): String {
        val totalSeconds = millis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60

        return "%02d:%02d".format(minutes, seconds)
    }

    private fun renderTimer(state: TaskDetailState) {
        val finishAt = state.task?.finishAt

        binding.tvTimer.text = if (finishAt == null) {
            "00:00"
        } else {
            val remainingMillis = finishAt - System.currentTimeMillis()
            if (remainingMillis <= 0L) {
                "00:00"
            } else {
                formatRemainingTime(remainingMillis)
            }
        }
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
            binding.tvTypeTask.text = "Type: ${task.type}"
            binding.tvCompleteMode.text = "Complete Mode: ${task.completeMode}"
            binding.tvDate.text = "Date: ${task.date}"
            binding.tvDifficulty.text = "Difficulty: ${task.difficulty}"
            binding.tvReward.text = "Reward: ${task.reward.toDisplayString()}"
            binding.tvPenalty.text = "Penalty: ${task.penalty.toDisplayString()}"
            binding.tvRequiredDurationMinutes.text = "Required Duration Minutes: ${task.requiredDurationMinutes}"

            binding.btnEdit.setOnClickListener {
                (activity as? MainActivity)?.openTaskForm(task.id)
            }
            binding.btnDelete.setOnClickListener {
                val dialog = SoftDeleteDialog.newInstance("Do you want to detele this task?")

                dialog.setOnConfirmListener {
                    viewModel.softDeleteTask(task.id)
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }

                dialog.show(parentFragmentManager, "SoftDeleteDialog")
            }
            binding.btnSuccess.setOnClickListener {
                viewModel.completeTask(task, task.completeMode,task.reward.items)

                if (task.completeMode == "tap") {
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }
            binding.btnBack.setOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }


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

                    state.timerStartEvent?.let { event ->
                        TaskTimerService.start(
                            context = requireContext(),
                            taskId = event.taskId,
                            finishAt = event.finishAt
                        )

                        viewModel.clearTimerStartEvent()
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                while (true) {
                    renderTimer(viewModel.state.value)
                    delay(1000L)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}