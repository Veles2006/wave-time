package com.sae.wavetime.data.repository

import com.sae.wavetime.data.dao.ItemDao
import com.sae.wavetime.data.mapper.toDomainList
import com.sae.wavetime.data.mapper.toRewardSelectList
import com.sae.wavetime.data.model.domain.Item
import com.sae.wavetime.ui.model.RewardSelectUiModel

class ItemRepository(
    private val itemDao: ItemDao
) {
    suspend fun getItems(): List<Item> {
        return itemDao
            .getAll()
            .toDomainList()
    }

    suspend fun getRewardSelects(): List<RewardSelectUiModel> {
        return itemDao
            .getAll()
            .toRewardSelectList()
    }
}