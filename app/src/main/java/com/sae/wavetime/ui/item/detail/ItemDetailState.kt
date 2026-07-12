package com.sae.wavetime.ui.item.detail

import com.sae.wavetime.ui.common.UiText
import com.sae.wavetime.ui.model.InventoryDetailUiModel

data class ItemDetailState(
    val isLoading: Boolean = false,
    val inventoryItem: InventoryDetailUiModel? = null,
    val error: String? = null,
    val quantity: Int = 0,
    val notificationMessage: UiText? = null
)
