package com.sae.wavetime.ui.state

import com.sae.wavetime.data.model.Item

data class ItemListState(
    val isLoading: Boolean = false,
    val items: List<Item> = emptyList(),
    val error: String? = null,
)
