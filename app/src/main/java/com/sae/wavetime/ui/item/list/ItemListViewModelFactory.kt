package com.sae.wavetime.ui.item.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sae.wavetime.data.repository.InventoryRepository
import com.sae.wavetime.data.repository.ItemRepository
import com.sae.wavetime.domain.usecase.UseItemUseCase
import com.sae.wavetime.ui.item.list.ItemListViewModel

class ItemListViewModelFactory(
    private val inventoryRepo: InventoryRepository,
    private val useItemUseCase: UseItemUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(ItemListViewModel::class.java)) {
            return ItemListViewModel(inventoryRepo, useItemUseCase) as T
        }
        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}