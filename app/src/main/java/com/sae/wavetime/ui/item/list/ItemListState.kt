package com.sae.wavetime.ui.item.list

import com.sae.wavetime.ui.model.InventoryUiModel

data class ItemListState(
    val isLoading: Boolean = false,
    val items: List<InventoryUiModel> = emptyList(),
    val error: String? = null,
)