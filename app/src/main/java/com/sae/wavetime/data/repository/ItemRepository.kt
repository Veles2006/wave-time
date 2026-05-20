package com.sae.wavetime.data.repository

import com.sae.wavetime.data.dao.ItemDao
import com.sae.wavetime.data.mapper.toDomain
import com.sae.wavetime.data.mapper.toDomainList
import com.sae.wavetime.data.mapper.toRewardSelectList
import com.sae.wavetime.data.model.entity.ItemEntity
import com.sae.wavetime.domain.model.Block
import com.sae.wavetime.domain.model.BlockOfItem
import com.sae.wavetime.domain.model.Item
import com.sae.wavetime.domain.model.KeyInfoPopulated
import com.sae.wavetime.ui.model.RewardSelectUiModel
import java.util.UUID

class ItemRepository(
    private val itemDao: ItemDao
) {
    private val keyColors = listOf(
        "white",
        "gray",
        "green",
        "blue",
        "purple",
        "yellow",
        "red",
        "black"
    )

    private val durationMinutesOfKey = listOf(
        10,
        30,
        60,
        120,
        240,
        480,
        1440,
        10080
    )

    suspend fun getItems(): List<Item> {
        return itemDao
            .getVisibleItems()
            .toDomainList()
    }

    suspend fun getItemById(id: String): Item? {
        return itemDao.getItemById(id)?.toDomain()
    }
    suspend fun getRewardSelects(): List<RewardSelectUiModel> {
        return itemDao
            .getVisibleItems()
            .toRewardSelectList()
    }

    suspend fun createKeysForBlock(block: Block) {
        keyColors.forEachIndexed { index, color ->
            val keyName = "${color.replaceFirstChar { it.uppercase() }} Access Key"
            val description = "Unlocks ${block.appName} temporarily. Higher-rank keys provide stronger access privileges."

            val item = ItemEntity(
                id = UUID.randomUUID().toString(),
                name = "$keyName · ${block.appName}",
                tier = color,
                rank = index + 1,
                category = "key",
                blockId = block.id,
                keyInfo = KeyInfoPopulated(
                    blockId = BlockOfItem(
                        id = block.id,
                        appName = block.appName,
                        packageName = block.packageName,
                        blockType = block.blockType
                    ),
                    isMaster = false,
                    durationMinutes = durationMinutesOfKey[index]
                ),
                description = description,
            )
            itemDao.insert(item)
        }
    }
}