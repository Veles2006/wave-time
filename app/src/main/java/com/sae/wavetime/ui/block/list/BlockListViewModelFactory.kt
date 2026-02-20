package com.sae.wavetime.ui.block.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sae.wavetime.data.repository.AppRepository
import com.sae.wavetime.ui.model.AppUiModel

class BlockListViewModelFactory(
    private val repository: AppRepository
): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(BlockListViewModel::class.java)) {
            return BlockListViewModel(repository) as T
        }
        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}