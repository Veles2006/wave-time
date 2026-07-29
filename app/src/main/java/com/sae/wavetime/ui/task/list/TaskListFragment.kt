package com.sae.wavetime.ui.task.list

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sae.wavetime.MainActivity
import com.sae.wavetime.R
import com.sae.wavetime.analytics.AnalyticsTracker
import com.sae.wavetime.data.repository.InventoryRepository
import com.sae.wavetime.data.repository.TaskRepository
import com.sae.wavetime.databinding.FragmentTaskListBinding
import com.sae.wavetime.domain.model.Task
import com.sae.wavetime.domain.usecase.CompleteTaskUseCase
import com.sae.wavetime.domain.usecase.StartTimerTaskUseCase
import com.sae.wavetime.engine.service.TaskTimerService
import com.sae.wavetime.local.DatabaseProvider
import com.sae.wavetime.ui.common.UiText
import com.sae.wavetime.ui.dialog.SoftDeleteDialog
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.sae.wavetime.ui.common.asString

class TaskListFragment : Fragment(R.layout.fragment_task_list) {

    private lateinit var adapter: TaskAdapter
    private var _binding: FragmentTaskListBinding? = null
    private val binding get() = _binding!!

    private var notificationJob: Job? = null

    private val viewModel: TaskListViewModel by activityViewModels  {
        val db = DatabaseProvider.getDatabase(requireContext())

        val taskRepo = TaskRepository(
            db.taskDao(),
            db.taskTemplateDao()
        )
        val inventoryRepo = InventoryRepository(db.inventoryDao())

        TaskListViewModelFactory(
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
            )
        )
    }

    private fun showTaskOptionsDialog(task: Task) {
        val options = arrayOf(getString(R.string.edit), getString(R.string.delete))

        AlertDialog.Builder(requireContext())
            .setTitle(task.name)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        (activity as? MainActivity)?.openTaskForm(task.id)
                    }

                    1 -> {
                        val dialog = SoftDeleteDialog.newInstance(
                            title = getString(R.string.delete_task_title),
                            message = getString(R.string.delete_task_message)
                        )

                        dialog.setOnConfirmListener {
                            viewModel.softDeleteTask(task.id)
                        }

                        if (parentFragmentManager.findFragmentByTag("SoftDeleteDialog") == null) {
                            dialog.show(parentFragmentManager, "SoftDeleteDialog")
                        }
                    }
                }
            }
            .show()
    }

    private fun setupSwipeCompleteTask() {
        val swipeCallback = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(
                viewHolder: RecyclerView.ViewHolder,
                direction: Int
            ) {
                val position = viewHolder.bindingAdapterPosition

                if (position == RecyclerView.NO_POSITION) return

                val task = adapter.getTaskAt(position)

                viewModel.completeTask(task)

                // Cho item quay lại vị trí cũ ngay
                adapter.notifyItemChanged(position)
            }
        }

        ItemTouchHelper(swipeCallback)
            .attachToRecyclerView(binding.rvTasks)
    }

    private fun showNotificationMessage(message: String) {
        notificationJob?.cancel()

        binding.notificationCard.visibility = View.VISIBLE
        binding.tvNotificationMessage.text = message

        binding.notificationCard.apply {
            visibility = View.VISIBLE
            alpha = 0f
            translationY = 80f

            animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(250)
                .start()

            setOnClickListener {
                hideNotificationMessage()
            }
        }

        notificationJob = viewLifecycleOwner.lifecycleScope.launch {
            delay(5000)

            hideNotificationMessage()
        }
    }

    private fun hideNotificationMessage() {
        notificationJob?.cancel()
        notificationJob = null

        binding.notificationCard.apply {
            animate().cancel()

            animate()
                .alpha(0f)
                .translationY(80f)
                .setDuration(250)
                .withEndAction {
                    visibility = View.GONE
                    viewModel.clearNotificationMessage()
                }
                .start()
        }
    }

    private fun formatRemainingTime(millis: Long): String {
        val totalSeconds = millis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60

        return "%02d:%02d".format(minutes, seconds)
    }

    private fun render(state: TaskListState) {

        adapter.submitList(state.tasks)

        if (state.tasks.isEmpty()) {
            binding.rvTasks.visibility = View.GONE
            binding.tvEmptyTask.visibility = View.VISIBLE
            binding.ivEmptyTask.visibility = View.VISIBLE
        } else {
            binding.rvTasks.visibility = View.VISIBLE
            binding.tvEmptyTask.visibility = View.GONE
            binding.ivEmptyTask.visibility = View.GONE
        }

        binding.layoutTimerInfo.isVisible = state.runningTimer.task != null

        binding.tvTimerTitle.text = getString(R.string.running_timer_task_title)

        binding.tvTimerTaskName.text =
            state.runningTimer.task?.name ?: getString(R.string.til_task_name)

        binding.tvTimer.text = getString(
            R.string.remaining_time_format,
            formatRemainingTime(state.runningTimer.remainingMillis)
        )
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentTaskListBinding.bind(view)

        adapter = TaskAdapter(
            onLongClick = { task ->
                showTaskOptionsDialog(task)
            },
            openTaskDetail = { taskId ->
                (activity as? MainActivity)?.openTaskDetail(taskId)
            }
        )

        binding.rvTasks.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTasks.adapter = adapter


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

                    val message = state.notificationMessage?.asString(requireContext())

                    Log.d("dd", "$message")

                    if (message != null) {
                        showNotificationMessage(message)
                    }
                }
            }
        }

        binding.btnCreateTask.setOnClickListener {
            (activity as? MainActivity)?.openTaskForm()
        }

        setupSwipeCompleteTask()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hideNotificationMessage()
        _binding = null
    }
}