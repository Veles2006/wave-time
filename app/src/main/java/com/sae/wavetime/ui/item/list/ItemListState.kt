package com.sae.wavetime.ui.item.list

import com.sae.wavetime.data.model.api.Item

data class ItemListState(
    val isLoading: Boolean = false,
    val items: List<Item> = emptyList(),
    val error: String? = null,
)