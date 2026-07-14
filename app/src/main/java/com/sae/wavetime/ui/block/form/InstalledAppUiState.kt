package com.sae.wavetime.ui.block.form

import com.sae.wavetime.ui.model.AppUiModel

data class InstalledAppUiState(
    val isLoading: Boolean = true,
    val apps: List<AppUiModel> = emptyList(),
    val searchQuery: String = "",
    val error: String? = null
)