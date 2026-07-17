package com.sae.wavetime.ui.task.form

import com.sae.wavetime.ui.common.UiText
import com.sae.wavetime.ui.model.RewardSelectUiModel

data class RewardSelectionState(
    val allRewards: List<RewardSelectUiModel> = emptyList(),
    val message: UiText? = null
)