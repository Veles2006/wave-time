package com.sae.wavetime.ui.block.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sae.wavetime.data.repository.BlockRepository

class BlockDetailViewModelFactory(
    private val blockRepo: BlockRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BlockDetailViewModel::class.java)) {
            return BlockDetailViewModel(blockRepo) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}