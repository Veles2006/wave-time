package com.sae.wavetime.ui.task.form

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sae.wavetime.R
import com.sae.wavetime.analytics.AnalyticsLogger
import com.sae.wavetime.data.mapper.toRewardSelectUiModelList
import com.sae.wavetime.domain.model.Task
import com.sae.wavetime.data.repository.ItemRepository
import com.sae.wavetime.data.repository.TaskRepository
import com.sae.wavetime.domain.task.TaskRewardLimits
import com.sae.wavetime.ui.common.UiText
import com.sae.wavetime.ui.common.toDifficultyInt
import com.sae.wavetime.ui.common.toTierInt
import com.sae.wavetime.ui.model.RewardSelectUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TaskFormViewModel(
    private val taskRepo: TaskRepository,
    private val itemRepo: ItemRepository,
    private val analyticsLogger: AnalyticsLogger
) : ViewModel() {
    private val _state = MutableStateFlow(TaskFormState())
    val state: StateFlow<TaskFormState> = _state

    fun loadRewards() {
        viewModelScope.launch {

            _state.update { it.copy(isLoading = true, error = null) }

            try {
                val rewards: List<RewardSelectUiModel> = itemRepo.getRewardSelects()

                _state.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        rewardSelection = currentState.rewardSelection.copy(
                            allRewards = rewards
                        )
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Unknown error",
                    )
                }
            }
        }
    }

    fun initRewardSelection(
        rewards: List<RewardSelectUiModel>,
        difficulty: Int
    ) {
        _state.update { currentState ->
            currentState.copy(
                difficulty = difficulty,
                rewardSelection = currentState.rewardSelection.copy(
                    allRewards = rewards
                )
            )
        }
    }

    fun observeTask(id: String) {
        viewModelScope.launch {
            taskRepo.getTaskByIdFlow(id)
                .catch { e ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = e.message
                        )
                    }
                }
                .collect { task ->
                    _state.update { currentState ->

                        val taskRewards =
                            task?.reward?.items
                                ?.toRewardSelectUiModelList()
                                .orEmpty()

                        val updatedAllRewards =
                            mergeSelectedRewards(
                                allRewards =
                                    currentState.rewardSelection.allRewards,
                                selectedRewards = taskRewards
                            )

                        currentState.copy(
                            isLoading = false,
                            task = task,
                            difficulty =
                                task?.difficulty?.toDifficultyInt()
                                    ?: currentState.difficulty,
                            rewardSelection =
                                currentState.rewardSelection.copy(
                                    allRewards = updatedAllRewards
                                ),
                            error =
                                if (task == null) {
                                    "Task not found"
                                } else {
                                    null
                                }
                        )
                    }
                }
        }
    }

    fun onDifficultyChanged(newDifficulty: Int) {
        _state.update { currentState ->

            val difficulty = newDifficulty.coerceIn(1, 8)

            val normalizedRewards = normalizeRewardsForDifficulty(
                rewards = currentState.rewardSelection.allRewards,
                difficulty = difficulty
            )

            val maxCoin = TaskRewardLimits.getMaxCoin(difficulty)
            val maxExperience =
                TaskRewardLimits.getMaxExperience(difficulty)

            currentState.copy(
                difficulty = difficulty,

                coin = currentState.coin?.coerceAtMost(maxCoin),

                experience = currentState.experience
                    ?.coerceAtMost(maxExperience),

                rewardSelection = currentState.rewardSelection.copy(
                    allRewards = normalizedRewards
                )
            )
        }
    }

    fun increaseQuantity(target: RewardSelectUiModel) {
        _state.update { currentState ->

            val isTierInsufficient =
                target.tier.toTierInt() > currentState.difficulty

            val hasReachedQuantityLimit =
                currentState.totalSelectedQuantity >=
                        currentState.maxSelectableQuantity

            when {
                isTierInsufficient -> {
                    return@update currentState.copy(
                        rewardSelection = currentState.rewardSelection.copy(
                            message = UiText.StringResource(
                                R.string.error_task_level_insufficient
                            )
                        )
                    )
                }

                hasReachedQuantityLimit -> {
                    return@update currentState.copy(
                        rewardSelection = currentState.rewardSelection.copy(
                            message = UiText.StringResource(
                                R.string.error_reward_quantity_limit_reached
                            )
                        )
                    )
                }
            }

            val updateRewards =
                currentState.rewardSelection.allRewards.map { reward ->
                    if (reward.id == target.id) {
                        reward.copy(
                            selectedQuantity =
                                reward.selectedQuantity + 1
                        )
                    } else {
                        reward
                    }
                }

            currentState.copy(
                rewardSelection = currentState.rewardSelection.copy(
                    allRewards = updateRewards
                )
            )
        }
    }

    fun decreaseQuantity(target: RewardSelectUiModel) {
        _state.update { currentState ->

            val updateRewards =
                currentState.rewardSelection.allRewards.map { reward ->
                    if (reward.id == target.id) {
                        reward.copy(
                            selectedQuantity =
                                (reward.selectedQuantity - 1)
                                    .coerceAtLeast(0)
                        )
                    } else {
                        reward
                    }
                }

            currentState.copy(
                rewardSelection = currentState.rewardSelection.copy(
                    allRewards = updateRewards
                )
            )
        }
    }

    fun addTask(task: Task) {
        viewModelScope.launch {
            try {
                taskRepo.insertTask(task)
                taskRepo.insertTaskTemplate(task)

                analyticsLogger.logTaskCreated(
                    completeMode = task.completeMode,
                    taskType = task.type
                )
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = e.message ?: "Unknown error"
                    )
                }
            }
        }
    }

    fun updateFullTask(task: Task) {
        viewModelScope.launch {
            taskRepo.updateFullTask(task)
        }
    }

    fun updateTaskReward(
        newRewards: List<RewardSelectUiModel>
    ) {
        _state.update { currentState ->
            val quantityById = newRewards.associate { reward ->
                reward.id to reward.selectedQuantity
            }

            val updatedRewards =
                currentState.rewardSelection.allRewards.map { reward ->
                    reward.copy(
                        selectedQuantity =
                            quantityById[reward.id] ?: 0
                    )
                }

            currentState.copy(
                rewardSelection =
                    currentState.rewardSelection.copy(
                        allRewards = updatedRewards
                    )
            )
        }
    }

    fun updateCoin(value: Int) {
        _state.update { currentState ->
            currentState.copy(
                coin = value.coerceIn(
                    minimumValue = 0,
                    maximumValue = currentState.maxCoin
                )
            )
        }
    }

    fun updateExperience(value: Int) {
        _state.update { currentState ->
            currentState.copy(
                experience = value.coerceIn(
                    minimumValue = 0,
                    maximumValue = currentState.maxExperience
                )
            )
        }
    }

    fun initForm(taskId: String?) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    error = null
                )
            }

            try {
                val allRewards = itemRepo.getRewardSelects()

                if (taskId == null) {
                    _state.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            rewardSelection =
                                currentState.rewardSelection.copy(
                                    allRewards = allRewards
                                )
                        )
                    }

                    return@launch
                }

                observeTaskWithRewards(
                    taskId = taskId,
                    allRewards = allRewards
                )
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Unknown error"
                    )
                }
            }
        }
    }

    fun clearRewardSelectionMessage() {
        _state.update { currentState ->
            currentState.copy(
                rewardSelection = currentState.rewardSelection.copy(
                    message = null
                )
            )
        }
    }

    fun clearRewardValues() {
        _state.update { currentState ->
            currentState.copy(
                coin = null,
                experience = null
            )
        }
    }

    fun onRewardSearchQueryChanged(query: String) {
        _state.update { currentState ->
            currentState.copy(
                rewardSearchQuery = query
            )
        }
    }

    fun clearRewardSearchQuery() {
        _state.update { currentState ->
            currentState.copy(
                rewardSearchQuery = ""
            )
        }
    }

    private suspend fun observeTaskWithRewards(
        taskId: String,
        allRewards: List<RewardSelectUiModel>
    ) {
        taskRepo.getTaskByIdFlow(taskId)
            .catch { e ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Unknown error"
                    )
                }
            }
            .collect { task ->

                val taskRewards =
                    task?.reward?.items
                        ?.toRewardSelectUiModelList()
                        .orEmpty()

                val mergedRewards = mergeSelectedRewards(
                    allRewards = allRewards,
                    selectedRewards = taskRewards
                )

                _state.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        task = task,
                        rewardSelection =
                            currentState.rewardSelection.copy(
                                allRewards = mergedRewards
                            ),
                        error = if (task == null) {
                            "Task not found"
                        } else {
                            null
                        }
                    )
                }
            }
    }

    private fun normalizeRewardsForDifficulty(
        rewards: List<RewardSelectUiModel>,
        difficulty: Int
    ): List<RewardSelectUiModel> {
        var remainingQuantity = difficulty

        return rewards.map { reward ->

            val normalizedQuantity = when {
                // Tier không hợp lệ
                reward.tier.toTierInt() > difficulty -> 0

                // Đã hết số lượng được phép chọn
                remainingQuantity < 0 -> 0

                else -> {
                    reward.selectedQuantity.coerceAtMost(remainingQuantity)
                }
            }

            remainingQuantity -= normalizedQuantity

            reward.copy(
                selectedQuantity = normalizedQuantity
            )
        }
    }

    private fun mergeSelectedRewards(
        allRewards: List<RewardSelectUiModel>,
        selectedRewards: List<RewardSelectUiModel>
    ): List<RewardSelectUiModel> {

        val selectedQuantityById =
            selectedRewards.associate { reward ->
                reward.id to reward.selectedQuantity
            }

        return allRewards.map { reward ->
            reward.copy(
                selectedQuantity =
                    selectedQuantityById[reward.id] ?: 0
            )
        }
    }
}
