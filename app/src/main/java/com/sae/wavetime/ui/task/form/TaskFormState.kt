package com.sae.wavetime.ui.task.form

import com.sae.wavetime.data.model.api.Task
import com.sae.wavetime.ui.model.RewardSelectUiModel

data class TaskFormState(
    val isLoading: Boolean = false,
    val error: String? = null,

    val availableRewards: List<RewardSelectUiModel> = emptyList(),
    val selectedRewards: List<RewardSelectUiModel> = emptyList(),

    val task: Task? = null
)