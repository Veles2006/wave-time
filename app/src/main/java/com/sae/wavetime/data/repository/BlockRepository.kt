package com.sae.wavetime.data.repository

import com.sae.wavetime.data.dao.BlockDao
import com.sae.wavetime.data.mapper.toDomain
import com.sae.wavetime.data.mapper.toDomainList
import com.sae.wavetime.data.mapper.toEntity
import com.sae.wavetime.data.resolver.AppIconResolver
import com.sae.wavetime.data.resolver.InstalledAppResolver
import com.sae.wavetime.domain.model.Block
import com.sae.wavetime.ui.model.AppUiModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class BlockRepository(
    private val blockDao: BlockDao,
    private val appIconResolver: AppIconResolver,
    private val installedAppResolver: InstalledAppResolver
) {
    fun getBlocksFlow(): Flow<List<AppUiModel>> {
        return blockDao
            .getAllFlow()
            .map { list ->
                list.map { block ->
                    AppUiModel(
                        id = block.id,
                        appName = block.appName,
                        packageName = block.packageName,
                        icon = appIconResolver.getIcon(block.packageName),
                        isActive = block.isActive
                    )
                }
            }
    }

    fun getBlockByIdFlow(id: String): Flow<AppUiModel?> {
        return blockDao.getByIdFlow(id)
            .map { block ->
                block?.let {
                    AppUiModel(
                        id= it.id,
                        appName = it.appName,
                        packageName = it.packageName,
                        icon = appIconResolver.getIcon(it.packageName),
                        penaltyMinutes = it.penaltyMinutes,
                        unlockUntil = it.unlockUntil,
                        reactivateAt = it.reactivateAt,
                        isActive = it.isActive
                    )
                }
            }
    }

    fun observeBlockCandidates(): Flow<List<Block>> {
        return blockDao
            .observeBlockCandidates()
            .map { list ->
                list.toDomainList()
            }
    }

    fun observeDeletedBlocks(): Flow<List<Block>> {
        return blockDao
            .observeDeletedBlocks()
            .map { list ->
                list.toDomainList()
            }
    }

    fun observeBlockNameById(
        blockId: String
    ): Flow<String?> {
        return blockDao.observeBlockNameById(blockId)
    }

    suspend fun getByPackageName(packageName: String): Block? {
        return blockDao.getByPackageName(packageName)?.toDomain()
    }

    suspend fun setActive(id: String, isChecked: Boolean) {
        blockDao.toggleActive(id, isChecked)
    }

    suspend fun saveBlock(block: Block): Block {

        val existedBlock = blockDao.getByPackageName(block.packageName)


        return if (existedBlock == null) {
            val newBlock = block.copy(
                id = UUID.randomUUID().toString(),
                isDeleted = false
            )
            blockDao.insert(newBlock.toEntity())
            newBlock
        } else if (existedBlock.id == block.id) {
            blockDao.updateFull(
                id = existedBlock.id,
                appName = block.appName,
                packageName = block.packageName,
                blockType = block.blockType,
                penaltyMinutes = block.penaltyMinutes,
                isActive = true,
                isDeleted = block.isDeleted
            )

            block
        }
        else {

            val restoredBlock = existedBlock.toDomain().copy(
                appName = block.appName,
                blockType = block.blockType,
                isActive = true,
                isDeleted = false
            )

            blockDao.updateFull(
                id = restoredBlock.id,
                appName = restoredBlock.appName,
                packageName = restoredBlock.packageName,
                blockType = restoredBlock.blockType,
                penaltyMinutes = restoredBlock.penaltyMinutes,
                isActive = restoredBlock.isActive,
                isDeleted = restoredBlock.isDeleted
            )

            restoredBlock
        }
    }

    suspend fun reactivateBlockIfDue(
        blockId: String,
        now: Long
    ): Boolean {
        return blockDao.reactivateBlockIfDue(
            blockId = blockId,
            now = now
        ) > 0
    }

    suspend fun setUnlockUntil(
        blockId: String,
        unlockUntil: Long
    ) {

        blockDao.setUnlockUntil(
            id = blockId,
            unlockUntil = unlockUntil
        )
    }

    // Delete method
    suspend fun softDelete(id: String) {
        blockDao.softDelete(id)
    }
}