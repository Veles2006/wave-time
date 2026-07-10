package com.sae.wavetime.ui.task.detail

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.sae.wavetime.MainActivity
import com.sae.wavetime.R
import com.sae.wavetime.analytics.AnalyticsTracker
import com.sae.wavetime.data.repository.InventoryRepository
import com.sae.wavetime.data.repository.TaskRepository
import com.sae.wavetime.databinding.FragmentTaskDetailBinding
import com.sae.wavetime.domain.usecase.CompleteTaskUseCase
import com.sae.wavetime.domain.usecase.StartTimerTaskUseCase
import com.sae.wavetime.domain.usecase.StopTimerTaskUseCase
import com.sae.wavetime.engine.service.TaskTimerService
import com.sae.wavetime.local.DatabaseProvider
import com.sae.wavetime.ui.common.toCompleteModeText
import com.sae.wavetime.ui.common.toDifficultyText
import com.sae.wavetime.ui.common.toStatusText
import com.sae.wavetime.ui.common.toTaskTypeText
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
                db,
                analyticsLogger = AnalyticsTracker(requireContext())
            ),
            StartTimerTaskUseCase(
                taskRepo,
                db
            ),
            StopTimerTaskUseCase(
                requireContext(),
                taskRepo
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

        if (state.isLoading || finishAt == null) {
            return
        }

        val remainingMillis = finishAt - System.currentTimeMillis()

        binding.tvTimer.text =
            if (remainingMillis <= 0L) {
                "00:00"
            } else {
                formatRemainingTime(remainingMillis)
            }
    }

    private fun render(state: TaskDetailState) {
        val task = state.task

        binding.tvTimer.isVisible = !state.isLoading && task?.finishAt != null
        binding.tvTimerTitle.isVisible = state.isLoading || task?.finishAt != null
        binding.layoutTimerInfo.isVisible = state.isLoading || task?.finishAt != null
        binding.btnStop.isVisible = state.isLoading || task?.finishAt != null

        if (state.error != null) {
            binding.tvTimer.isVisible = false
            binding.tvTimerTitle.isVisible = false
            binding.layoutTimerInfo.isVisible = false
            return
        }

        if (task == null) return

        binding.tvTaskName.text = task.name
        binding.tvDescription.text =
            getString(
                R.string.task_description_format,
                if (task.description.isNullOrBlank())
                    getString(R.string.none)
                else
                    task.description
            )

        binding.tvStatus.text =
            getString(R.string.task_status_format, task.status.toStatusText(requireContext()))

        binding.tvTypeTask.text =
            getString(R.string.task_type_format, task.type.toTaskTypeText(requireContext()))

        binding.tvCompleteMode.text =
            getString(R.string.task_complete_mode_format, task.completeMode.toCompleteModeText(requireContext()))

        binding.tvDate.text =
            getString(R.string.task_date_format, task.date ?: getString(R.string.none))

        binding.tvDifficulty.text =
            getString(R.string.task_difficulty_format, task.difficulty.toDifficultyText(requireContext()))

        binding.tvReward.text =
            getString(R.string.task_reward_format, task.reward.toDisplayString(requireContext()))

        binding.tvPenalty.text =
            getString(R.string.task_penalty_format, task.penalty.toDisplayString(requireContext()))

        binding.tvRequiredDurationMinutes.text =
            getString(
                R.string.task_required_duration_format,
                task.requiredDurationMinutes ?: 0
            )

        binding.btnEdit.setOnClickListener {
            (activity as? MainActivity)?.openTaskForm(task.id)
        }

        binding.tvTimerTitle.isVisible = task.finishAt != null

        binding.btnDelete.setOnClickListener {
            val dialog = SoftDeleteDialog.newInstance(
                title = getString(R.string.delete_task_title),
                message = getString(R.string.delete_task_message)
            )

            dialog.setOnConfirmListener {
                viewModel.softDeleteTask(task.id)
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }

            if (parentFragmentManager.findFragmentByTag("SoftDeleteDialog") == null) {
                dialog.show(parentFragmentManager, "SoftDeleteDialog")
            }
        }
        binding.btnSuccess.isVisible =
            task.status != "in_progress" &&
                    task.status != "completed"

        Log.d("dd", "${task.status}")

        binding.btnSuccess.setOnClickListener {
            viewModel.completeTask(task)

            if (task.completeMode == "tap") {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }

        binding.btnStop.setOnClickListener {
            viewModel.stopRunningTimerTask(taskId)
            binding.tvTimer.text = getString(R.string.task_timer_default)
        }

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentTaskDetailBinding.bind(view)

        taskId = requireArguments().getString("taskId")
            ?: throw IllegalArgumentException("Missing taskId")

        viewModel.observeTask(taskId)
        viewModel.observeRunningTimerTask()


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