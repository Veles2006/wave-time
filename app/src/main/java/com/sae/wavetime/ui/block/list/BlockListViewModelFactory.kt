package com.sae.wavetime.ui.block.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sae.wavetime.data.repository.BlockRepository
import com.sae.wavetime.data.resolver.AppIconResolver
import com.sae.wavetime.domain.usecase.ActivateBlockUseCase
import com.sae.wavetime.domain.usecase.DeleteBlockUseCase
import com.sae.wavetime.domain.usecase.TemporarilyDeactivateBlockUseCase

class BlockListViewModelFactory(
    private val blockRepo: BlockRepository,
    private val activateBlockUseCase: ActivateBlockUseCase,
    private val temporarilyDeactivateBlockUseCase: TemporarilyDeactivateBlockUseCase,
    private val deleteBlockUseCase: DeleteBlockUseCase
): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(BlockListViewModel::class.java)) {
            return BlockListViewModel(
                blockRepo,
                activateBlockUseCase,
                temporarilyDeactivateBlockUseCase,
                deleteBlockUseCase
            ) as T
        }
        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}