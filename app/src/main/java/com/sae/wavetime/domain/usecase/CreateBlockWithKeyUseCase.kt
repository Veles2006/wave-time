package com.sae.wavetime.domain.usecase

import com.sae.wavetime.data.repository.BlockRepository
import com.sae.wavetime.data.repository.ItemRepository
import com.sae.wavetime.domain.model.Block

class CreateBlockWithKeyUseCase(
    private val blockRepo: BlockRepository,
    private val itemRepo: ItemRepository
) {
    suspend operator fun invoke(block: Block) {
        val existed = blockRepo.getByPackageName(block.packageName)

        val savedBlock = blockRepo.saveBlock(block)

        if (existed == null) {
            itemRepo.createKeysForBlock(savedBlock)
        }
    }
}