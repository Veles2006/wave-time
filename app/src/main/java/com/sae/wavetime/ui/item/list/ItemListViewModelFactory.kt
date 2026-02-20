package com.sae.wavetime.ui.item.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sae.wavetime.data.repository.ItemRepository
import com.sae.wavetime.ui.item.list.ItemListViewModel

class ItemListViewModelFactory(
    private val repository: ItemRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(ItemListViewModel::class.java)) {
            return ItemListViewModel(repository) as T
        }
        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}