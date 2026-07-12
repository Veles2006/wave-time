package com.sae.wavetime.ui.item.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sae.wavetime.data.repository.ItemRepository
import com.sae.wavetime.domain.usecase.ObserveInventoryDetailUseCase
import com.sae.wavetime.domain.usecase.UseItemUseCase

class ItemDetailViewModelFactory(
    private val itemRepo: ItemRepository,
    private val useItemUseCase: UseItemUseCase,
    private val observeInventoryDetailUseCase: ObserveInventoryDetailUseCase
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(ItemDetailViewModel::class.java)) {
            return ItemDetailViewModel(itemRepo, useItemUseCase, observeInventoryDetailUseCase) as T
        }
        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}