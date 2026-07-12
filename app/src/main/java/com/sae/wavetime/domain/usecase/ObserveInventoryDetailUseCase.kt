package com.sae.wavetime.domain.usecase

import com.sae.wavetime.data.repository.BlockRepository
import com.sae.wavetime.data.repository.InventoryRepository
import com.sae.wavetime.ui.model.InventoryDetailUiModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class ObserveInventoryDetailUseCase(
    private val inventoryRepository: InventoryRepository,
    private val blockRepository: BlockRepository,
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(
        itemId: String
    ): Flow<InventoryDetailUiModel?> {
        return inventoryRepository
            .observeInventoryItemByItemId(itemId)
            .flatMapLatest { inventoryItem ->
                when {
                    inventoryItem == null -> {
                        flowOf(null)
                    }

                    inventoryItem.blockId == null -> {
                        flowOf(inventoryItem)
                    }

                    else -> {
                        blockRepository
                            .observeBlockNameById(inventoryItem.blockId)
                            .map { blockName ->
                                inventoryItem.copy(
                                    blockName = blockName
                                )
                            }
                    }
                }
            }
    }
}