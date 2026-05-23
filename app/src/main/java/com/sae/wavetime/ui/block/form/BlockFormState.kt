package com.sae.wavetime.ui.block.form

import com.sae.wavetime.ui.model.AppUiModel

data class BlockFormState(
    val isLoading: Boolean = false,
    val app: AppUiModel? = null,
    val error: String? = null,
    val selectedApp: AppUiModel? = null
)
