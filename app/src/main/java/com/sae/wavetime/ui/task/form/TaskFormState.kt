package com.sae.wavetime.ui.task.form

import com.sae.wavetime.domain.model.Task
import com.sae.wavetime.domain.task.TaskRewardLimits
import com.sae.wavetime.ui.common.toTierInt
import com.sae.wavetime.ui.model.RewardSelectUiModel

data class TaskFormState(
    val isLoading: Boolean = false,
    val error: String? = null,

    val difficulty: Int = 1,
    val coin: Int? = null,
    val experience: Int? = null,
    val rewardSelection: RewardSelectionState = RewardSelectionState(),

    val task: Task? = null
) {
    val availableRewards: List<RewardSelectUiModel>
        get() = rewardSelection.allRewards.filter { reward ->
            reward.tier.toTierInt() <= difficulty
        }

    val selectedRewards: List<RewardSelectUiModel>
        get() = rewardSelection.allRewards.filter { reward ->
            reward.selectedQuantity > 0
        }

    val totalSelectedQuantity: Int
        get() = rewardSelection.allRewards.sumOf { reward ->
            reward.selectedQuantity
        }

    val maxSelectableQuantity: Int
        get() = difficulty

    val maxCoin: Int
        get() = TaskRewardLimits.getMaxCoin(difficulty)

    val maxExperience: Int
        get() = TaskRewardLimits.getMaxExperience(difficulty)
}