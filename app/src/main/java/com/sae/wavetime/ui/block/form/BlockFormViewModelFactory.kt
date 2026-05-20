package com.sae.wavetime.ui.block.form

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sae.wavetime.data.repository.BlockRepository
import com.sae.wavetime.domain.usecase.CreateBlockWithKeyUseCase

class BlockFormViewModelFactory(
    private val blockRepo: BlockRepository,
    private val createBlockWithKeyUseCase: CreateBlockWithKeyUseCase
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BlockFormViewModel::class.java)) {
            return BlockFormViewModel(blockRepo, createBlockWithKeyUseCase) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}