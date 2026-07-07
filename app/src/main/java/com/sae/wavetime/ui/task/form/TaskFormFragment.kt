package com.sae.wavetime.ui.task.form

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.sae.wavetime.R
import com.sae.wavetime.analytics.AnalyticsTracker
import com.sae.wavetime.data.mapper.toRewardItemList
import com.sae.wavetime.domain.model.Penalty
import com.sae.wavetime.domain.model.Reward
import com.sae.wavetime.domain.model.Task
import com.sae.wavetime.data.repository.ItemRepository
import com.sae.wavetime.data.repository.TaskRepository
import com.sae.wavetime.databinding.FragmentTaskFormBinding
import com.sae.wavetime.local.DatabaseProvider
import com.sae.wavetime.ui.common.toDifficultyText
import kotlinx.coroutines.launch
import java.time.LocalDate
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
    private var taskType: String = "default"
    private var completeMode: String = "tap"

    private val viewModel: TaskFormViewModel by viewModels {
        val db = DatabaseProvider.getDatabase(requireContext())
        TaskFormViewModelFactory(
            TaskRepository(
                db.taskDao(),
                db.taskTemplateDao()
            ),
            ItemRepository(
                db.itemDao()
            ),
            analyticsLogger = AnalyticsTracker(requireContext())
        )
    }
    private fun render(state: TaskFormState) {

        if (state.isLoading) {
            // show loading
        }

        if (state.error != null) {
            // show error
        }
        val filteredList = state.selectedRewards

        binding.rvItems.visibility =
            if (filteredList.isEmpty()) View.GONE else View.VISIBLE

        binding.btnItem.text = if (filteredList.isEmpty())
            getString(R.string.not_set)
        else
            getString(R.string.change_item)

        adapter.submitList(filteredList)


        if (taskId == null) {
            binding.tvTitle.text = getString(R.string.create_task)
        } else {
            binding.tvTitle.text = getString(R.string.edit_task)
            if (taskId != null && binding.edtTaskName.text.isNullOrEmpty()) {
                state.task?.let { task ->
                    binding.edtTaskName.setText(task.name)
                    binding.edtTaskDesc.setText(task.description)
                    binding.btnCoin.text = task.reward.gold.toString()
                    binding.btnExp.text = task.reward.exp.toString()
                    binding.btnItem.text = getString(R.string.change_item)

                    coinValue = task.reward.gold
                    expValue = task.reward.exp

                    taskType = task.type

                    task.requiredDurationMinutes?.let {
                        binding.edtTimer.setText(it.toString())
                    }

                    binding.rgTaskType.check(
                        when (task.type) {
                            "daily" -> R.id.rbDailyType
                            else -> R.id.rbDefaultType
                        }
                    )

                    binding.rgCompleteMode.check(
                        when (task.completeMode) {
                            "timer" -> R.id.rbTimerMode
                            else -> R.id.rbTapMode
                        }
                    )
                }
            }
            task = state.task
        }
    }

    private fun isNotificationEnabled(): Boolean {
        val appNotificationEnabled =
            NotificationManagerCompat.from(requireContext()).areNotificationsEnabled()

        val runtimePermissionGranted =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }

        return appNotificationEnabled && runtimePermissionGranted
    }

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                // Người dùng đã bật notification
            } else {
                showNotificationSettingsDialog()
            }
        }
    private fun requestNotificationPermissionIfNeeded() {
        if (isNotificationEnabled()) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            showNotificationSettingsDialog()
        }
    }

    private fun showNotificationSettingsDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Bật thông báo")
            .setMessage("Wave Time cần thông báo để nhắc bạn khi timer hoàn thành nhiệm vụ.")
            .setPositiveButton("Mở cài đặt") { _, _ ->
                openAppNotificationSettings()
            }
            .setNegativeButton("Để sau", null)
            .show()
    }

    private fun openAppNotificationSettings() {
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
            }
        } else {
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse("package:${requireContext().packageName}")
            }
        }

        startActivity(intent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentTaskFormBinding.bind(view)

        taskId = arguments?.getString("taskId")


        adapter = TaskFormRewardAdapter()

        binding.rvItems.layoutManager = LinearLayoutManager(requireContext())
        binding.rvItems.adapter = adapter

        binding.sliderDifficulty.setLabelFormatter { value ->
            value.toInt().toDifficultyText(requireContext())
        }

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
            val dialog = CoinExperienceDialog(
                title = getString(R.string.set_coin),
                message = getString(R.string.enter_coin_amount),
                unit = getString(R.string.coin),
                errorMessage = getString(R.string.please_enter_value)
            ) { text ->
                coinValue = text.toIntOrNull() ?: 0
                binding.btnCoin.text = getString(R.string.coin_format, coinValue)
            }

            if (parentFragmentManager.findFragmentByTag("CoinDialog") == null) {
                dialog.show(parentFragmentManager, "CoinDialog")
            }
        }

        binding.btnExp.setOnClickListener {
            val dialog = CoinExperienceDialog(
                title = getString(R.string.set_exp),
                message = getString(R.string.enter_exp_amount),
                unit = getString(R.string.exp),
                errorMessage = getString(R.string.please_enter_value)
            ) { text ->
                expValue = text.toIntOrNull() ?: 0
                binding.btnExp.text = getString(R.string.exp_format, expValue)
            }

            if (parentFragmentManager.findFragmentByTag("ExpDialog") == null) {
                dialog.show(parentFragmentManager, "ExpDialog")
            }
        }

        binding.btnItem.setOnClickListener {
            val dialog = SelectItemDialog { selectedList ->
                viewModel.updateTaskReward(selectedList)
            }
            dialog.show(parentFragmentManager, "SelectItem")
        }

        binding.rgTaskType.setOnCheckedChangeListener { _, checkedId ->
            taskType = when (checkedId) {
                R.id.rbDefaultType -> "default"
                R.id.rbDailyType -> "daily"
                else -> "default"
            }
        }

        binding.rgCompleteMode.setOnCheckedChangeListener { _, checkedId ->
            completeMode = when (checkedId) {
                R.id.rbTapMode -> "tap"
                R.id.rbTimerMode -> "timer"
                else -> "tap"
            }

            binding.layoutTimer.visibility =
                if (completeMode == "timer") View.VISIBLE else View.GONE
        }

        binding.btnSuccess.setOnClickListener {

            val completeMode = when (binding.rgCompleteMode.checkedRadioButtonId) {
                R.id.rbTimerMode -> "timer"
                else -> "tap"
            }

            if (completeMode == "timer" && !isNotificationEnabled()) {
                requestNotificationPermissionIfNeeded()
                return@setOnClickListener
            }

            val taskName = binding.edtTaskName.text.toString().trim()
            val taskDesc = binding.edtTaskDesc.text?.toString()?.trim()
            val taskItemReward = viewModel.state.value.selectedRewards
            val requiredDurationMinutes = binding.edtTimer.text.toString().trim().toIntOrNull()

            val taskDifficulty = when (binding.sliderDifficulty.value.toInt()) {
                1 -> "Mortal"
                2 -> "Yao"
                3 -> "Gui"
                4 -> "Mara"
                5 -> "Sage"
                6 -> "Xian"
                7 -> "Deity"
                8 -> "Creation"
                else -> "Unknown"
            }

            if (taskName.isBlank()) {
                binding.edtTaskName.error = "This field cannot be empty"
                return@setOnClickListener
            }


            if (
                completeMode == "timer" &&
                (requiredDurationMinutes == null || requiredDurationMinutes <= 0)
                ) {
                binding.edtTimer.error = "Please enter a valid timer"
                return@setOnClickListener
            }

            if (taskId == null) {
                val taskData = Task(
                    id = UUID.randomUUID().toString(),
                    name = taskName,
                    description = taskDesc,
                    status = "pending",
                    type = taskType,
                    completeMode = completeMode,
                    requiredDurationMinutes = requiredDurationMinutes,
                    difficulty = taskDifficulty,
                    reward = Reward(
                        gold = coinValue,
                        exp = expValue,
                        items = taskItemReward.toRewardItemList()
                    ),
                    penalty = Penalty(),
                    date = LocalDate.now().toString()
                )
                viewModel.addTask(taskData)
            } else {
                val taskData = task?.copy(
                    name = taskName,
                    description = taskDesc,
                    type = taskType,
                    completeMode = completeMode,
                    requiredDurationMinutes = requiredDurationMinutes,
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